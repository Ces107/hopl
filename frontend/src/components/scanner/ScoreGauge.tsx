interface Props {
  score: number;
  size?: number;
}

export default function ScoreGauge({ score, size = 200 }: Props) {
  const radius = (size - 20) / 2;
  const circumference = 2 * Math.PI * radius;
  const progress = (score / 100) * circumference;
  const color = score >= 80 ? '#22c55e' : score >= 50 ? '#eab308' : '#ef4444';
  const bgColor = score >= 80 ? '#dcfce7' : score >= 50 ? '#fef9c3' : '#fef2f2';
  const label = score >= 80 ? 'Low Risk' : score >= 50 ? 'Medium Risk' : 'High Risk';

  return (
    <div className="flex flex-col items-center gap-3">
      <div className="relative" style={{ width: size, height: size }}>
        <svg width={size} height={size} className="-rotate-90">
          <circle cx={size / 2} cy={size / 2} r={radius} fill="none"
            stroke="#e5e7eb" strokeWidth="12" />
          <circle cx={size / 2} cy={size / 2} r={radius} fill="none"
            stroke={color} strokeWidth="12" strokeLinecap="round"
            strokeDasharray={circumference}
            strokeDashoffset={circumference - progress}
            className="transition-all duration-1000 ease-out" />
        </svg>
        <div className="absolute inset-0 flex flex-col items-center justify-center">
          <span className="text-5xl font-bold" style={{ color }}>{score}</span>
          <span className="text-sm text-gray-500">/100</span>
        </div>
      </div>
      <span className="inline-flex items-center rounded-full px-3 py-1 text-sm font-medium"
        style={{ backgroundColor: bgColor, color }}>
        {label}
      </span>
    </div>
  );
}
