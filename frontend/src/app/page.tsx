export default function Home() {
  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 font-sans flex flex-col justify-between p-8 md:p-16">
      <header className="flex items-center justify-between border-b border-slate-800 pb-6">
        <div className="flex items-center gap-3">
          <span className="text-3xl">🎬</span>
          <h1 className="text-2xl font-bold tracking-tight text-white">
            EditHub <span className="text-xs bg-indigo-500/20 text-indigo-400 border border-indigo-500/30 px-2 py-0.5 rounded-full font-normal">Pre-Alpha</span>
          </h1>
        </div>
        <span className="text-sm text-slate-400 font-mono">GitHub for Video Editing</span>
      </header>

      <main className="max-w-4xl mx-auto my-12 w-full space-y-8">
        <section className="text-center space-y-4">
          <h2 className="text-4xl md:text-5xl font-extrabold tracking-tight bg-gradient-to-r from-indigo-400 via-purple-400 to-pink-400 bg-clip-text text-transparent">
            Open Video Collaboration Platform
          </h2>
          <p className="text-slate-400 text-lg max-w-2xl mx-auto">
            Where creators upload raw footage and editing briefs, and video editors contribute edits, manage version trees, and build portfolios.
          </p>
        </section>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="p-6 bg-slate-900/60 border border-slate-800 rounded-xl space-y-2">
            <div className="text-indigo-400 text-xl">📦 Repository → Project</div>
            <p className="text-slate-400 text-sm">
              Creators upload raw footage, audio, and asset packs with project briefs.
            </p>
          </div>

          <div className="p-6 bg-slate-900/60 border border-slate-800 rounded-xl space-y-2">
            <div className="text-purple-400 text-xl">🌿 Branch → Version</div>
            <p className="text-slate-400 text-sm">
              Editors branch from raw footage, edit locally, and submit version preview edits.
            </p>
          </div>

          <div className="p-6 bg-slate-900/60 border border-slate-800 rounded-xl space-y-2">
            <div className="text-pink-400 text-xl">🔀 Pull Request → Merge</div>
            <p className="text-slate-400 text-sm">
              Creators leave timeline comments, request changes, and accept final edits into history.
            </p>
          </div>
        </div>

        <div className="p-6 bg-slate-900/80 border border-slate-800 rounded-xl font-mono text-sm space-y-3">
          <div className="flex items-center justify-between text-slate-300 border-b border-slate-800 pb-2">
            <span>System Infrastructure Status</span>
            <span className="text-emerald-400 flex items-center gap-1.5">
              <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
              Initialized
            </span>
          </div>
          <div className="grid grid-cols-2 gap-4 text-xs text-slate-400 pt-1">
            <div>Backend API: <span className="text-slate-200">Spring Boot (Java 21)</span></div>
            <div>Database: <span className="text-slate-200">PostgreSQL 16</span></div>
            <div>Cache & Queue: <span className="text-slate-200">Redis 7</span></div>
            <div>Storage: <span className="text-slate-200">MinIO (S3-compatible)</span></div>
          </div>
        </div>
      </main>

      <footer className="border-t border-slate-800 pt-6 text-center text-xs text-slate-500 font-mono">
        EditHub Architecture Baseline &bull; Built with Next.js, Spring Boot & Docker Compose
      </footer>
    </div>
  );
}
