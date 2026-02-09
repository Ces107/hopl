package com.hopl.service;

import com.hopl.dto.scan.ScanResponseDto;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

@Component
public class ComplianceAnalyzer {

    private static final Pattern PRIVACY_PATTERN = Pattern.compile(
            "(?i)(privacy|privacidad|datenschutz|confidentialit|privacidade|politique.*confidentialit)", Pattern.CASE_INSENSITIVE);
    private static final Pattern TERMS_PATTERN = Pattern.compile(
            "(?i)(terms|condiciones|nutzungsbedingungen|conditions.*utilisation|termos)", Pattern.CASE_INSENSITIVE);
    private static final Pattern COOKIE_BANNER_PATTERN = Pattern.compile(
            "(?i)(cookie-consent|cookie-banner|cookie-notice|cookieconsent|cc-window|gdpr|onetrust|cookiebot|quantcast)", Pattern.CASE_INSENSITIVE);
    private static final Pattern CONTACT_PATTERN = Pattern.compile(
            "(?i)(contact|contacto|kontakt|mailto:|@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})", Pattern.CASE_INSENSITIVE);
    private static final Pattern TRACKER_PATTERN = Pattern.compile(
            "(?i)(google-analytics|googletagmanager|gtag|fbq|facebook.*pixel|hotjar|mixpanel|segment\\.com|analytics\\.js)", Pattern.CASE_INSENSITIVE);

    /**
     * Analyzes a parsed HTML document for compliance issues.
     *
     * @param doc the Jsoup document
     * @param url the original URL
     * @return analysis result with score, issues, and jurisdiction
     */
    public AnalysisResult analyze(Document doc, String url) {
        List<ScanResponseDto.IssueDto> issues = new ArrayList<>();
        Map<String, Object> details = new HashMap<>();
        int totalPoints = 0;
        int earnedPoints = 0;

        String fullHtml = doc.html();
        String bodyText = doc.body() != null ? doc.body().text() : "";
        Elements allLinks = doc.select("a[href]");
        Elements allScripts = doc.select("script[src], script");

        // 1. Privacy Policy check
        boolean hasPrivacy = checkLinkExists(allLinks, PRIVACY_PATTERN);
        issues.add(createIssue("MISSING_PRIVACY_POLICY", "Missing Privacy Policy",
                "Your website does not have a visible Privacy Policy link. Required by GDPR, CCPA, and most data protection laws.",
                15, hasPrivacy));
        totalPoints += 15;
        if (hasPrivacy) earnedPoints += 15;
        details.put("hasPrivacyPolicy", hasPrivacy);

        // 2. Terms of Service check
        boolean hasTerms = checkLinkExists(allLinks, TERMS_PATTERN);
        issues.add(createIssue("MISSING_TERMS", "Missing Terms of Service",
                "No Terms of Service or Terms and Conditions link was found on your website.",
                10, hasTerms));
        totalPoints += 10;
        if (hasTerms) earnedPoints += 10;
        details.put("hasTerms", hasTerms);

        // 3. Cookie Consent Banner check
        boolean hasCookieConsent = COOKIE_BANNER_PATTERN.matcher(fullHtml).find();
        issues.add(createIssue("MISSING_COOKIE_CONSENT", "Missing Cookie Consent Banner",
                "No cookie consent mechanism detected. GDPR requires explicit consent before setting non-essential cookies.",
                15, hasCookieConsent));
        totalPoints += 15;
        if (hasCookieConsent) earnedPoints += 15;
        details.put("hasCookieConsent", hasCookieConsent);

        // 4. Contact Information check
        boolean hasContact = CONTACT_PATTERN.matcher(fullHtml).find() ||
                checkLinkExists(allLinks, CONTACT_PATTERN);
        issues.add(createIssue("NO_CONTACT_INFO", "No Contact Information",
                "No visible contact email, form, or address found. Most regulations require users to be able to contact you.",
                8, hasContact));
        totalPoints += 8;
        if (hasContact) earnedPoints += 8;
        details.put("hasContactInfo", hasContact);

        // 5. Third-party Tracking check
        boolean hasTrackers = false;
        for (Element script : allScripts) {
            String src = script.attr("src");
            String inline = script.data();
            if (TRACKER_PATTERN.matcher(src).find() || TRACKER_PATTERN.matcher(inline).find()) {
                hasTrackers = true;
                break;
            }
        }
        boolean trackersDisclosed = hasTrackers && (hasPrivacy || hasCookieConsent);
        boolean trackerIssue = hasTrackers && !trackersDisclosed;
        issues.add(createIssue("THIRD_PARTY_COOKIES", "Third-Party Tracking Without Disclosure",
                "Third-party scripts (analytics, ads, pixels) detected but not disclosed in a privacy or cookie policy.",
                12, !trackerIssue));
        totalPoints += 12;
        if (!trackerIssue) earnedPoints += 12;
        details.put("hasTrackers", hasTrackers);
        details.put("trackersDisclosed", trackersDisclosed);

        // 6. HTTPS check
        boolean isHttps = url.startsWith("https://");
        issues.add(createIssue("NO_HTTPS", "Not Using HTTPS",
                "Your website is not served over HTTPS. Unencrypted connections put user data at risk.",
                10, isHttps));
        totalPoints += 10;
        if (isHttps) earnedPoints += 10;
        details.put("isHttps", isHttps);

        // 7. Cookie Policy check (separate from banner)
        boolean hasCookiePolicy = false;
        for (Element link : allLinks) {
            String href = link.attr("href").toLowerCase();
            String text = link.text().toLowerCase();
            if ((href.contains("cookie") && (href.contains("policy") || href.contains("politic"))) ||
                    (text.contains("cookie") && (text.contains("policy") || text.contains("politic")))) {
                hasCookiePolicy = true;
                break;
            }
        }
        issues.add(createIssue("MISSING_COOKIE_POLICY", "Missing Cookie Policy",
                "Cookies are being set but no separate Cookie Policy page was found.",
                8, hasCookiePolicy || !hasTrackers));
        totalPoints += 8;
        if (hasCookiePolicy || !hasTrackers) earnedPoints += 8;
        details.put("hasCookiePolicy", hasCookiePolicy);

        // 8. Forms without disclosure check
        Elements forms = doc.select("form");
        boolean hasForms = !forms.isEmpty();
        boolean formsHaveDisclosure = !hasForms || hasPrivacy;
        issues.add(createIssue("NO_DATA_COLLECTION_DISCLOSURE", "No Data Collection Disclosure",
                "Forms collecting user data found but no disclosure about what data is collected or how it's used.",
                10, formsHaveDisclosure));
        totalPoints += 10;
        if (formsHaveDisclosure) earnedPoints += 10;
        details.put("hasForms", hasForms);

        // 9. Opt-out mechanism check
        boolean hasOptOut = fullHtml.toLowerCase().contains("unsubscribe") ||
                fullHtml.toLowerCase().contains("opt-out") ||
                fullHtml.toLowerCase().contains("opt out") ||
                fullHtml.toLowerCase().contains("darse de baja");
        issues.add(createIssue("NO_OPT_OUT", "No Opt-Out Mechanism",
                "No unsubscribe or opt-out mechanism found for marketing communications.",
                7, hasOptOut || !hasForms));
        totalPoints += 7;
        if (hasOptOut || !hasForms) earnedPoints += 7;

        // 10. Accessibility basics check
        Elements images = doc.select("img");
        long imagesWithAlt = images.stream().filter(img -> !img.attr("alt").isBlank()).count();
        boolean hasBasicAccessibility = images.isEmpty() || (double) imagesWithAlt / images.size() > 0.5;
        issues.add(createIssue("NO_ACCESSIBILITY_BASICS", "Missing Basic Accessibility",
                "Basic accessibility features (alt text on images) are missing from key elements.",
                5, hasBasicAccessibility));
        totalPoints += 5;
        if (hasBasicAccessibility) earnedPoints += 5;
        details.put("totalImages", images.size());
        details.put("imagesWithAlt", imagesWithAlt);

        // Calculate score
        int score = totalPoints > 0 ? (int) Math.round((double) earnedPoints / totalPoints * 100) : 0;

        // Detect jurisdiction
        String jurisdiction = detectJurisdiction(doc, url);
        details.put("detectedJurisdiction", jurisdiction);

        return new AnalysisResult(score, issues, details, jurisdiction);
    }

    private boolean checkLinkExists(Elements links, Pattern pattern) {
        for (Element link : links) {
            String href = link.attr("href");
            String text = link.text();
            if (pattern.matcher(href).find() || pattern.matcher(text).find()) {
                return true;
            }
        }
        return false;
    }

    private String detectJurisdiction(Document doc, String url) {
        String html = doc.html().toLowerCase();
        String tld = extractTld(url);

        if (tld != null) {
            if (Set.of("de", "fr", "es", "it", "nl", "be", "at", "pt", "pl", "se", "fi", "dk",
                    "ie", "gr", "cz", "ro", "hu", "bg", "hr", "sk", "si", "lt", "lv", "ee",
                    "cy", "lu", "mt", "eu").contains(tld)) {
                return "EU_GDPR";
            }
            if ("uk".equals(tld) || "co.uk".equals(tld)) return "UK_DPA";
            if ("br".equals(tld) || "com.br".equals(tld)) return "BR_LGPD";
            if ("ca".equals(tld)) return "CA_PIPEDA";
            if ("au".equals(tld) || "com.au".equals(tld)) return "AU_PRIVACY";
        }

        if (html.contains("gdpr") || html.contains("rgpd") || html.contains("dsgvo")) return "EU_GDPR";
        if (html.contains("ccpa") || html.contains("california")) return "US_CCPA";
        if (html.contains("lgpd")) return "BR_LGPD";

        return "GLOBAL";
    }

    private String extractTld(String url) {
        try {
            String host = new java.net.URI(url).getHost();
            if (host == null) return null;
            String[] parts = host.split("\\.");
            return parts.length > 1 ? parts[parts.length - 1] : null;
        } catch (Exception e) {
            return null;
        }
    }

    private ScanResponseDto.IssueDto createIssue(String code, String title, String description, int severity, boolean passed) {
        return new ScanResponseDto.IssueDto(code, title, description, severity, passed);
    }

    public static class AnalysisResult {
        private final int score;
        private final List<ScanResponseDto.IssueDto> issues;
        private final Map<String, Object> details;
        private final String jurisdiction;

        public AnalysisResult(int score, List<ScanResponseDto.IssueDto> issues, Map<String, Object> details, String jurisdiction) {
            this.score = score;
            this.issues = issues;
            this.details = details;
            this.jurisdiction = jurisdiction;
        }

        public int getScore() { return score; }
        public List<ScanResponseDto.IssueDto> getIssues() { return issues; }
        public Map<String, Object> getDetails() { return details; }
        public String getJurisdiction() { return jurisdiction; }
    }
}
