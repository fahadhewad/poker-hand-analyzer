import { useState } from 'react';
import type { ChangeEvent } from 'react';
import type { AnalysisResponse, PlayerReport } from './types';
import './App.css';

export default function App() {
  const [result, setResult] = useState<AnalysisResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [fileName, setFileName] = useState<string | null>(null);

  async function onFile(e: ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0];
    if (!file) return;
    setFileName(file.name);
    setLoading(true);
    setError(null);
    setResult(null);
    try {
      const text = await file.text();
      const res = await fetch('/api/analyze', {
        method: 'POST',
        headers: { 'Content-Type': 'text/plain' },
        body: text,
      });
      if (!res.ok) {
        const body = await res.json().catch(() => ({}));
        throw new Error(body.error || `Server returned ${res.status}`);
      }
      const data: AnalysisResponse = await res.json();
      setResult(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="app">
      <header>
        <h1>Poker Hand Analyzer</h1>
        <p>
          Upload a PokerStars hand-history text file to see per-player stats,
          player-type classification, and detected leaks.
        </p>
      </header>

      <section className="upload">
        <label className="upload-btn">
          <input type="file" accept=".txt" onChange={onFile} />
          <span>Choose hand history file</span>
        </label>
        {fileName && <span className="file-name">{fileName}</span>}
      </section>

      {loading && <p className="status">Analyzing…</p>}
      {error && <p className="status error">{error}</p>}

      {result && <ResultsView data={result} />}
    </div>
  );
}

function ResultsView({ data }: { data: AnalysisResponse }) {
  if (data.players.length === 0) {
    return <p className="status">No players found in this file.</p>;
  }
  return (
    <section className="results">
      <p className="summary">
        Analyzed <strong>{data.handsAnalyzed}</strong> hand{data.handsAnalyzed === 1 ? '' : 's'}
        {' '}across <strong>{data.players.length}</strong> player{data.players.length === 1 ? '' : 's'}.
      </p>
      <div className="player-grid">
        {data.players.map((p) => (
          <PlayerCard key={p.name} report={p} />
        ))}
      </div>
    </section>
  );
}

function PlayerCard({ report }: { report: PlayerReport }) {
  const typeClass = `type-${report.type.toLowerCase()}`;
  return (
    <article className="player-card">
      <div className="player-header">
        <h2>{report.name}</h2>
        <span className={`type-badge ${typeClass}`}>
          {report.type.replace('_', ' ')}
        </span>
      </div>
      <table className="stats-table">
        <tbody>
          <tr>
            <td>Hands</td>
            <td>{report.handsPlayed}</td>
          </tr>
          <tr>
            <td>VPIP</td>
            <td>{pct(report.vpip)}</td>
          </tr>
          <tr>
            <td>PFR</td>
            <td>{pct(report.pfr)}</td>
          </tr>
          <tr>
            <td>3-bet %</td>
            <td>{pct(report.threeBetPct)}</td>
          </tr>
          <tr>
            <td>AF</td>
            <td>
              {report.aggressionFactor == null
                ? '—'
                : report.aggressionFactor.toFixed(2)}
            </td>
          </tr>
        </tbody>
      </table>
      {report.leaks.length > 0 && (
        <div className="leaks">
          <h3>Leaks ({report.leaks.length})</h3>
          <ul>
            {report.leaks.map((leak, i) => (
              <li key={i} className={`leak severity-${leak.severity.toLowerCase()}`}>
                <div className="leak-header">
                  <span className="leak-type">{leak.type.replace('_', ' ')}</span>
                  <span className="leak-hand">
                    hand #{leak.handId} · {leak.street.toLowerCase()}
                  </span>
                </div>
                <p>{leak.explanation}</p>
              </li>
            ))}
          </ul>
        </div>
      )}
    </article>
  );
}

function pct(x: number) {
  return `${(x * 100).toFixed(1)}%`;
}
