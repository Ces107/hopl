import { Link } from 'react-router-dom';
import { Check, Zap } from 'lucide-react';

const plans = [
  {
    name: 'Quick Fix',
    price: '4.99',
    period: 'one-time',
    description: '1 AI-generated document',
    features: ['1 document of your choice', 'AI-powered customization', 'PDF download', 'Multi-language support'],
    cta: 'Get Started',
    popular: false,
  },
  {
    name: 'Full Compliance',
    price: '29.99',
    period: 'one-time',
    description: 'All documents for your website',
    features: ['All 15 document types', 'AI-powered customization', 'PDF download', 'Multi-jurisdiction', 'Embed-ready HTML', 'Multi-language support'],
    cta: 'Fix Everything',
    popular: true,
  },
  {
    name: 'Annual Guard',
    price: '49.99',
    period: '/year',
    description: 'Full compliance + annual updates',
    features: ['Everything in Full Compliance', 'Annual document updates', 'Law change notifications', 'Priority support', 'Re-generate anytime'],
    cta: 'Stay Protected',
    popular: false,
  },
  {
    name: 'Pro',
    price: '19.99',
    period: '/month',
    description: 'Unlimited websites & documents',
    features: ['Unlimited documents', 'Unlimited websites', 'Monthly re-scans', 'Priority support', 'API access', 'Team members'],
    cta: 'Go Pro',
    popular: false,
  },
];

export default function Pricing() {
  return (
    <div className="mx-auto max-w-6xl px-4 py-16">
      <div className="mb-12 text-center">
        <h1 className="mb-4 text-4xl font-extrabold text-gray-900 dark:text-white">
          Simple, Transparent Pricing
        </h1>
        <p className="text-lg text-gray-600 dark:text-gray-400">
          No hidden fees. No subscriptions required. Pay only for what you need.
        </p>
      </div>

      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
        {plans.map((plan) => (
          <div key={plan.name}
            className={`relative flex flex-col rounded-2xl border p-6 ${
              plan.popular
                ? 'border-2 border-brand-600 shadow-xl'
                : 'border-gray-200 dark:border-gray-800'
            }`}>
            {plan.popular && (
              <div className="absolute -top-3 left-1/2 -translate-x-1/2 rounded-full bg-brand-600 px-4 py-1 text-xs font-bold text-white">
                MOST POPULAR
              </div>
            )}
            <div className="mb-4">
              <h3 className="text-lg font-semibold text-gray-900 dark:text-white">{plan.name}</h3>
              <p className="text-sm text-gray-500">{plan.description}</p>
            </div>
            <div className="mb-6">
              <span className="text-4xl font-bold text-gray-900 dark:text-white">&euro;{plan.price}</span>
              <span className="text-gray-500">{plan.period !== 'one-time' ? plan.period : ''}</span>
              {plan.period === 'one-time' && <span className="ml-2 text-xs text-gray-400">one-time</span>}
            </div>
            <ul className="mb-8 flex-1 space-y-3">
              {plan.features.map((f) => (
                <li key={f} className="flex items-start gap-2 text-sm text-gray-600 dark:text-gray-400">
                  <Check className="mt-0.5 h-4 w-4 shrink-0 text-green-500" />
                  {f}
                </li>
              ))}
            </ul>
            <Link to="/register"
              className={`flex items-center justify-center gap-2 rounded-xl py-3 text-sm font-semibold transition ${
                plan.popular
                  ? 'bg-brand-600 text-white hover:bg-brand-700'
                  : 'bg-gray-100 text-gray-700 hover:bg-gray-200 dark:bg-gray-800 dark:text-gray-300'
              }`}>
              {plan.popular && <Zap className="h-4 w-4" />}
              {plan.cta}
            </Link>
          </div>
        ))}
      </div>

      {/* FAQ */}
      <div className="mt-20">
        <h2 className="mb-8 text-center text-2xl font-bold text-gray-900 dark:text-white">FAQ</h2>
        <div className="mx-auto max-w-3xl space-y-6">
          {[
            { q: 'Is the compliance scan really free?', a: 'Yes, 100% free. No credit card, no registration required. Scan any website instantly.' },
            { q: 'Is this legal advice?', a: 'No. HOPL generates document templates using AI. We recommend having a qualified attorney review any legal documents before use.' },
            { q: 'What regulations are covered?', a: 'We cover GDPR (EU), CCPA (US/California), LGPD (Brazil), PIPEDA (Canada), UK DPA, and Australian Privacy Act. Documents are auto-tailored to the detected jurisdiction.' },
            { q: 'Can I edit the generated documents?', a: 'Yes. All documents are generated in Markdown format and can be freely edited before downloading as PDF.' },
            { q: 'What payment methods do you accept?', a: 'We use Stripe for secure payments. All major credit cards, Apple Pay, and Google Pay are accepted.' },
          ].map(({ q, a }) => (
            <div key={q} className="rounded-xl border border-gray-200 p-6 dark:border-gray-800">
              <h3 className="mb-2 font-semibold text-gray-900 dark:text-white">{q}</h3>
              <p className="text-sm text-gray-600 dark:text-gray-400">{a}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
