import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import { Link, NavLink, Navigate, Route, Routes, useLocation, useNavigate, useParams } from 'react-router-dom';
import { Activity, ArrowDownRight, ArrowRight, ArrowUpRight, BriefcaseBusiness, Building2, CalendarDays, Check, ChevronDown, CircleHelp, Eye, EyeOff, Filter, LayoutDashboard, LogOut, Menu, Plus, Search, ShieldCheck, Sparkles, Users, X } from 'lucide-react';
import { applicationApi, authApi, errorMessage, interviewApi } from './api.js';

const AuthContext = createContext(null);
const STATUSES = ['SAVED', 'APPLIED', 'ASSESSMENT', 'INTERVIEW', 'OFFER', 'REJECTED'];
const OUTCOMES = ['SCHEDULED', 'PASSED', 'FAILED', 'CANCELLED'];

function useAuth() { return useContext(AuthContext); }
function pretty(value) { return value ? value.toLowerCase().replaceAll('_', ' ').replace(/\b\w/g, (letter) => letter.toUpperCase()) : '—'; }
function statusLabel(status) { return status === 'OFFER' ? 'Selected' : pretty(status); }
function dateLabel(value, options = { day: '2-digit', month: 'short', year: 'numeric' }) {
  if (!value) return '—';
  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? value : new Intl.DateTimeFormat(undefined, options).format(date);
}
function localDateTimeValue(value) {
  return value ? String(value).slice(0, 16) : '';
}
function initials(name = '') { return name.split(/\s+/).filter(Boolean).slice(0, 2).map((part) => part[0]).join('').toUpperCase() || 'AT'; }
function StatusBadge({ status }) { return <span className={`status-badge status-${String(status || '').toLowerCase()}`}>{statusLabel(status)}</span>; }
function Notice({ children, onClose }) { return children ? <div className="notice"><span>{children}</span>{onClose && <button className="icon-button" onClick={onClose} aria-label="Dismiss"><X size={15} /></button>}</div> : null; }

function App() {
  const [user, setUser] = useState(() => {
    try { return JSON.parse(localStorage.getItem('applytrack-user')) || null; } catch { return null; }
  });
  const [mobileMenu, setMobileMenu] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const authValue = useMemo(() => ({ user, setUser }), [user]);
  const publicPage = ['/login', '/register'].includes(location.pathname);

  useEffect(() => {
    if (user) localStorage.setItem('applytrack-user', JSON.stringify(user));
    else localStorage.removeItem('applytrack-user');
  }, [user]);

  async function logout() {
    try { await authApi.logout(); } catch { /* Clear local UI state even if the server session expired. */ }
    setUser(null);
    navigate('/login');
  }

  return <AuthContext.Provider value={authValue}>
    {user && !publicPage && <AppShell user={user} onLogout={logout} mobileMenu={mobileMenu} setMobileMenu={setMobileMenu} />}
    <Routes>
      <Route path="/login" element={user ? <Navigate to="/" replace /> : <LoginPage />} />
      <Route path="/register" element={user ? <Navigate to="/" replace /> : <RegisterPage />} />
      <Route path="/*" element={user ? <PrivatePages /> : <Navigate to="/login" replace />} />
    </Routes>
  </AuthContext.Provider>;
}

function AppShell({ user, onLogout, mobileMenu, setMobileMenu }) {
  const { pathname } = useLocation();
  const title = pathname.startsWith('/applications') ? 'Applications' : pathname.startsWith('/interviews') ? 'Interviews' : 'Dashboard';
  const navItems = [
    { to: '/', label: 'Dashboard', icon: LayoutDashboard, end: true },
    { to: '/applications', label: 'Applications', icon: BriefcaseBusiness },
    { to: '/interviews', label: 'Interviews', icon: CalendarDays },
  ];
  return <>
    {mobileMenu && <button className="sidebar-scrim" onClick={() => setMobileMenu(false)} aria-label="Close menu" />}
    <aside className={`sidebar ${mobileMenu ? 'sidebar-open' : ''}`}>
      <Link to="/" className="brand"><span className="brand-mark"><Activity size={18} strokeWidth={2.8} /></span><span>apply<span>track</span></span></Link>
      <div className="workspace-label">WORKSPACE</div>
      <nav className="side-nav">{navItems.map(({ to, label, icon: Icon, end }) => <NavLink key={to} to={to} end={end} onClick={() => setMobileMenu(false)} className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}><Icon size={18} /><span>{label}</span>{label === 'Applications' && <span className="nav-arrow">›</span>}</NavLink>)}</nav>
      <div className="sidebar-spacer" />
      <div className="sidebar-tip"><div className="tip-icon"><Sparkles size={16} /></div><b>Stay on track</b><p>Small steps add up to the right opportunity.</p><Link to="/applications/new">Add an application <ArrowRight size={13} /></Link></div>
      <div className="sidebar-bottom"><span className="online-dot" />Your data is private</div>
    </aside>
    <div className="main-area">
      <header className="topbar"><div className="topbar-left"><button className="menu-toggle icon-button" onClick={() => setMobileMenu(true)} aria-label="Open menu"><Menu size={20} /></button><div className="breadcrumb">Workspace <span>/</span> <b>{title}</b></div></div><div className="topbar-right"><span className="today-label"><CalendarDays size={15} />{dateLabel(new Date(), { weekday: 'short', month: 'short', day: 'numeric' })}</span><div className="topbar-divider" /><div className="profile"><span className="avatar">{initials(user.name)}</span><span className="profile-name">{user.name}</span><button className="icon-button logout-button" onClick={onLogout} title="Log out" aria-label="Log out"><LogOut size={17} /></button></div></div></header>
    </div>
  </>;
}

function PrivatePages() {
  return <main className="page-content"><Routes>
    <Route path="/" element={<DashboardPage />} />
    <Route path="/applications" element={<ApplicationsPage />} />
    <Route path="/applications/new" element={<ApplicationFormPage />} />
    <Route path="/applications/:id/edit" element={<ApplicationFormPage />} />
    <Route path="/applications/:id" element={<ApplicationDetailsPage />} />
    <Route path="/interviews" element={<InterviewsPage />} />
    <Route path="/interviews/new" element={<InterviewFormPage />} />
    <Route path="/interviews/:id/edit" element={<InterviewFormPage />} />
    <Route path="/interviews/:id" element={<InterviewDetailsPage />} />
    <Route path="*" element={<Navigate to="/" replace />} />
  </Routes></main>;
}

function AuthLayout({ children, subtitle }) {
  return <div className="auth-layout"><div className="auth-visual"><Link to="/login" className="brand brand-light"><span className="brand-mark"><Activity size={18} strokeWidth={2.8} /></span><span>apply<span>track</span></span></Link><div className="auth-pitch"><div className="pitch-label"><span /> YOUR NEXT CHAPTER STARTS HERE</div><h1>Make your next<br />move <em>count.</em></h1><p>One clear place to organize your search, track your progress, and show up ready.</p><div className="auth-mini-card"><div className="mini-orbit"><Activity size={20} /></div><div><b>Your search, in motion</b><span>Keep every opportunity moving forward.</span></div><ArrowUpRight size={17} /></div></div><div className="auth-visual-footer"><span>© 2026 ApplyTrack</span><span>Built for what’s next</span></div></div><section className="auth-panel"><div className="auth-mobile-brand"><span className="brand-mark"><Activity size={18} /></span>apply<span>track</span></div><div className="auth-card">{children}<p className="auth-note"><ShieldCheck size={14} />Your information stays private and secure</p></div><div className="auth-panel-footer">{subtitle}</div></section></div>;
}

function LoginPage() {
  const [form, setForm] = useState({ email: '', password: '' });
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);
  const { setUser } = useAuth();
  const navigate = useNavigate();
  async function submit(event) {
    event.preventDefault(); setError(''); setBusy(true);
    try {
      await authApi.login(form);
      setUser({ name: form.email.split('@')[0], email: form.email });
      navigate('/');
    } catch (exception) { setError(errorMessage(exception)); }
    finally { setBusy(false); }
  }
  return <AuthLayout subtitle="A calmer way to navigate your job search."><div className="auth-eyebrow">WELCOME BACK</div><h2>Log in to ApplyTrack</h2><p className="auth-copy">Pick up right where your job search left off.</p><Notice>{error}</Notice><form className="form-stack" onSubmit={submit}><label>Email address<input type="email" autoComplete="email" placeholder="you@example.com" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} required /></label><label>Password<span className="password-input-wrap"><input type={showPassword ? 'text' : 'password'} autoComplete="current-password" placeholder="Enter your password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} required /><button className="password-toggle" type="button" onClick={() => setShowPassword((visible) => !visible)} aria-label={showPassword ? 'Hide password' : 'Show password'} aria-pressed={showPassword}>{showPassword ? <EyeOff size={19} /> : <Eye size={19} />}</button></span></label><button className="primary-button auth-submit" disabled={busy}>{busy ? 'Logging in…' : 'Log in'}<ArrowRight size={16} /></button></form><p className="auth-switch">New to ApplyTrack? <Link to="/register">Create an account</Link></p></AuthLayout>;
}

function RegisterPage() {
  const [form, setForm] = useState({ fullname: '', email: '', password: '' });
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [busy, setBusy] = useState(false);
  const navigate = useNavigate();
  async function submit(event) {
    event.preventDefault(); setError(''); setSuccess(''); setBusy(true);
    try { await authApi.register(form); setSuccess('Your account was created. You can now log in.'); setTimeout(() => navigate('/login'), 1100); }
    catch (exception) { setError(errorMessage(exception)); }
    finally { setBusy(false); }
  }
  return <AuthLayout subtitle="Start with a little clarity. Build from there."><div className="auth-eyebrow">GET STARTED</div><h2>Create your account</h2><p className="auth-copy">A little organization goes a long way.</p><Notice>{error || success}</Notice><form className="form-stack" onSubmit={submit}><label>Full name<input value={form.fullname} onChange={(e) => setForm({ ...form, fullname: e.target.value })} autoComplete="name" placeholder="Your name" required /></label><label>Email address<input type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} autoComplete="email" placeholder="you@example.com" required /></label><label>Password<span className="password-input-wrap"><input type={showPassword ? 'text' : 'password'} value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} autoComplete="new-password" minLength={8} placeholder="At least 8 characters" required /><button className="password-toggle" type="button" onClick={() => setShowPassword((visible) => !visible)} aria-label={showPassword ? 'Hide password' : 'Show password'} aria-pressed={showPassword}>{showPassword ? <EyeOff size={19} /> : <Eye size={19} />}</button></span></label><button className="primary-button auth-submit" disabled={busy}>{busy ? 'Creating account…' : 'Create account'}<ArrowRight size={16} /></button></form><p className="auth-switch">Already have an account? <Link to="/login">Log in</Link></p></AuthLayout>;
}

function PageHeading({ eyebrow, title, description, action }) {
  return <div className="page-heading"><div><div className="eyebrow">{eyebrow}</div><h1>{title}</h1><p>{description}</p></div>{action}</div>;
}

function DashboardPage() {
  const { user } = useAuth();
  const [summary, setSummary] = useState({ total: 0, counts: {}, recent: [], upcoming: [] });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  useEffect(() => {
    let alive = true;
    Promise.all([
      applicationApi.list({ size: 1 }),
      ...STATUSES.map((status) => applicationApi.list({ size: 1, status })),
      applicationApi.list({ size: 5 }),
      interviewApi.upcoming(),
    ]).then(([all, ...rest]) => {
      if (!alive) return;
      const recent = rest[STATUSES.length];
      const upcoming = rest[STATUSES.length + 1];
      const counts = Object.fromEntries(STATUSES.map((status, index) => [status, rest[index].data.totalElements || 0]));
      setSummary({ total: all.data.totalElements || 0, counts, recent: recent.data.content || [], upcoming: upcoming.data || [] });
    }).catch((exception) => { if (alive) setError(errorMessage(exception)); })
      .finally(() => { if (alive) setLoading(false); });
    return () => { alive = false; };
  }, []);
  const cards = [
    { label: 'Total applications', value: summary.total, icon: BriefcaseBusiness, color: 'violet', note: 'Across your search' },
    { label: 'Applied', value: summary.counts.APPLIED || 0, icon: ArrowUpRight, color: 'blue', note: 'Applications sent' },
    { label: 'In interview', value: summary.counts.INTERVIEW || 0, icon: Users, color: 'orange', note: 'Moving forward' },
    { label: 'Selected', value: summary.counts.OFFER || 0, icon: Check, color: 'green', note: 'Offers received' },
    { label: 'Rejected', value: summary.counts.REJECTED || 0, icon: ArrowDownRight, color: 'rose', note: 'Closed applications' },
  ];
  return <>
    <PageHeading eyebrow="YOUR OVERVIEW" title={`Good ${greeting()}, ${user.name.split(' ')[0]}.`} description="Here’s where your job search stands today." action={<Link to="/applications/new" className="primary-button"><Plus size={17} /> Add application</Link>} />
    <Notice>{error}</Notice>
    <div className="stats-grid">{cards.map(({ label, value, icon: Icon, color, note }) => <div className="stat-card" key={label}><div className={`stat-icon ${color}`}><Icon size={18} /></div><div className="stat-label">{label}</div><div className="stat-value">{loading ? '—' : value}</div><div className="stat-note">{note}</div></div>)}</div>
    <div className="dashboard-grid"><section className="panel recent-panel"><div className="panel-heading"><div><h2>Recent applications</h2><p>Your latest opportunities at a glance.</p></div><Link className="text-link" to="/applications">View all <ArrowRight size={14} /></Link></div><ApplicationsTable applications={summary.recent} loading={loading} compact /></section>
      <section className="panel upcoming-panel"><div className="panel-heading"><div><h2>Upcoming interviews</h2><p>Be ready for what’s next.</p></div><Link className="circle-link" to="/interviews" aria-label="View interviews"><ArrowRight size={15} /></Link></div>
        {loading ? <div className="empty-inline">Loading interviews…</div> : summary.upcoming.length ? <div className="upcoming-list">{summary.upcoming.slice(0, 4).map((interview) => <div className="upcoming-item" key={interview.id}><div className="date-tile"><span>{new Date(interview.scheduledAt).toLocaleDateString(undefined, { month: 'short' })}</span><b>{new Date(interview.scheduledAt).getDate()}</b></div><div className="upcoming-info"><b>{interview.jobApplication?.companyname || 'Company'}</b><span>{interview.interviewRound} · {dateLabel(interview.scheduledAt, { hour: 'numeric', minute: '2-digit' })}</span></div><span className="upcoming-dot" /></div>)}</div> : <div className="empty-inline"><div className="empty-icon"><CalendarDays size={20} /></div><b>No upcoming interviews</b><span>Scheduled interviews will show up here.</span><Link to="/interviews/new">Schedule one <ArrowRight size={13} /></Link></div>}
      </section>
    </div>
    <div className="footnote"><CircleHelp size={14} />Counts reflect your application records. “Selected” uses the backend’s <code>OFFER</code> status.</div>
  </>;
}
function greeting() { const hour = new Date().getHours(); return hour < 12 ? 'morning' : hour < 18 ? 'afternoon' : 'evening'; }

function ApplicationsTable({ applications, loading, onDelete, onStatusChange, compact = false }) {
  if (loading) return <div className="table-state">Loading applications…</div>;
  if (!applications.length) return <div className="table-state"><div className="empty-icon"><BriefcaseBusiness size={20} /></div><b>No applications yet</b><span>Try another search or add your first application.</span></div>;
  return <div className="table-scroll"><table><thead><tr><th>COMPANY / ROLE</th><th>STATUS</th><th>APPLIED</th><th>{compact ? 'DETAILS' : 'ACTIONS'}</th></tr></thead><tbody>{applications.map((application) => <tr key={application.id}><td><div className="company-cell"><span className="company-icon"><Building2 size={16} /></span><span><b>{application.companyname}</b><small>{application.jobname}</small></span></div></td><td><StatusBadge status={application.status} /></td><td className="muted-cell">{dateLabel(application.appliedAt)}</td><td><div className="table-actions"><Link to={`/applications/${application.id}`} className="row-link">View</Link>{!compact && <><select className="status-select" aria-label={`Change status for ${application.companyname}`} value={application.status} onChange={(e) => onStatusChange?.(application.id, e.target.value)}>{STATUSES.map((status) => <option key={status} value={status}>{statusLabel(status)}</option>)}</select><button className="row-delete" onClick={() => onDelete?.(application)} title="Delete application" aria-label={`Delete ${application.companyname}`}><X size={15} /></button></>}</div></td></tr>)}</tbody></table></div>;
}

function ApplicationsPage() {
  const [data, setData] = useState({ content: [], totalPages: 0, totalElements: 0, number: 0 });
  const [company, setCompany] = useState('');
  const [status, setStatus] = useState('');
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [reloadKey, setReloadKey] = useState(0);
  const navigate = useNavigate();
  useEffect(() => {
    let alive = true; setLoading(true); setError('');
    applicationApi.list({ page, size: 10, company, status }).then(({ data: result }) => { if (alive) setData(result); })
      .catch((exception) => { if (alive) setError(errorMessage(exception)); })
      .finally(() => { if (alive) setLoading(false); });
    return () => { alive = false; };
  }, [page, company, status, reloadKey]);
  async function remove(application) {
    if (!window.confirm(`Delete the application at ${application.companyname}?`)) return;
    try { await applicationApi.remove(application.id); setReloadKey((key) => key + 1); }
    catch (exception) { setError(errorMessage(exception)); }
  }
  async function updateStatus(id, nextStatus) {
    try { await applicationApi.updateStatus(id, nextStatus); setReloadKey((key) => key + 1); }
    catch (exception) { setError(errorMessage(exception)); }
  }
  return <>
    <PageHeading eyebrow="YOUR OPPORTUNITIES" title="Applications" description="Keep every opportunity organized, from first application to final outcome." action={<Link to="/applications/new" className="primary-button"><Plus size={17} /> Add application</Link>} />
    <Notice>{error}</Notice>
    <section className="panel applications-panel"><div className="toolbar"><div className="search-wrap"><Search size={17} /><input value={company} onChange={(e) => { setCompany(e.target.value); if (e.target.value) setStatus(''); setPage(0); }} placeholder="Search companies…" aria-label="Search companies" /></div><label className="filter-select"><Filter size={15} /><select value={status} onChange={(e) => { setStatus(e.target.value); if (e.target.value) setCompany(''); setPage(0); }} aria-label="Filter by status"><option value="">All statuses</option>{STATUSES.map((value) => <option key={value} value={value}>{statusLabel(value)}</option>)}</select><ChevronDown size={14} /></label><span className="result-count">{data.totalElements} {data.totalElements === 1 ? 'application' : 'applications'}</span></div>
      <ApplicationsTable applications={data.content || []} loading={loading} onDelete={remove} onStatusChange={updateStatus} />
      <div className="pagination"><span>Page {data.totalPages ? data.number + 1 : 0} of {data.totalPages}</span><div><button disabled={page <= 0 || loading} onClick={() => setPage((current) => current - 1)}>Previous</button><button disabled={page + 1 >= data.totalPages || loading} onClick={() => setPage((current) => current + 1)}>Next</button></div></div>
    </section>
    <div className="footnote"><CircleHelp size={14} />Search and status filtering use the backend. Apply them one at a time because there is no combined search endpoint.</div>
  </>;
}

function ApplicationFormPage() {
  const { id } = useParams();
  const editing = Boolean(id);
  const [form, setForm] = useState({ companyname: '', jobname: '', jobdescription: '', status: 'APPLIED', appliedAt: new Date().toISOString().slice(0, 10) });
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);
  const [loading, setLoading] = useState(editing);
  const navigate = useNavigate();
  useEffect(() => {
    if (!editing) return;
    let alive = true;
    applicationApi.details(id).then(({ data }) => {
      if (alive) setForm({ companyname: data.companyname || '', jobname: data.jobname || '', jobdescription: data.jobdescription || '', status: data.status || 'APPLIED', appliedAt: data.appliedAt || '' });
    }).catch((exception) => { if (alive) setError(errorMessage(exception)); }).finally(() => { if (alive) setLoading(false); });
    return () => { alive = false; };
  }, [editing, id]);
  async function submit(event) {
    event.preventDefault(); setError(''); setBusy(true);
    try {
      if (editing) await applicationApi.update(id, form);
      else await applicationApi.create(form);
      navigate(editing ? `/applications/${id}` : '/applications');
    }
    catch (exception) { setError(errorMessage(exception)); }
    finally { setBusy(false); }
  }
  return <><PageHeading eyebrow={editing ? 'UPDATE DETAILS' : 'NEW OPPORTUNITY'} title={editing ? 'Edit application' : 'Add application'} description={editing ? 'Keep the opportunity details up to date.' : 'Capture the details now. You can update its progress as you go.'} />
    <section className="form-panel panel">{loading ? <div className="table-state">Loading application…</div> : <><Notice>{error}</Notice><form className="data-form" onSubmit={submit}><div className="form-grid"><label>Company name <span className="required">*</span><input maxLength={100} required value={form.companyname} onChange={(e) => setForm({ ...form, companyname: e.target.value })} placeholder="e.g. Infosys" /></label><label>Job title <span className="required">*</span><input maxLength={100} required value={form.jobname} onChange={(e) => setForm({ ...form, jobname: e.target.value })} placeholder="e.g. Java Developer" /></label><label>Application status <span className="required">*</span><select required value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}>{STATUSES.map((status) => <option key={status} value={status}>{statusLabel(status)}</option>)}</select></label><label>Date applied <span className="required">*</span><input type="date" required value={form.appliedAt} onChange={(e) => setForm({ ...form, appliedAt: e.target.value })} /></label><label className="full-field">Job description<textarea rows={5} value={form.jobdescription} onChange={(e) => setForm({ ...form, jobdescription: e.target.value })} placeholder="A few notes about the role…" /></label></div><div className="form-actions"><button type="button" className="secondary-button" onClick={() => navigate(-1)}>Cancel</button><button className="primary-button" disabled={busy}>{busy ? 'Saving…' : editing ? 'Save changes' : 'Save application'}<ArrowRight size={15} /></button></div></form></>}</section>
    <div className="footnote"><ShieldCheck size={14} />Your account is linked automatically. No user ID is needed.</div></>;
}

function ApplicationDetailsPage() {
  const { id } = useParams();
  const [application, setApplication] = useState(null);
  const [interviews, setInterviews] = useState([]);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  useEffect(() => {
    let alive = true;
    Promise.all([applicationApi.details(id), interviewApi.byApplication(id)]).then(([app, interviewList]) => { if (alive) { setApplication(app.data); setInterviews(interviewList.data); } })
      .catch((exception) => { if (alive) setError(errorMessage(exception)); }).finally(() => { if (alive) setLoading(false); });
    return () => { alive = false; };
  }, [id]);
  async function remove() {
    if (!window.confirm(`Delete the application at ${application.companyname}?`)) return;
    try { await applicationApi.remove(id); navigate('/applications'); }
    catch (exception) { setError(errorMessage(exception)); }
  }
  return <><PageHeading eyebrow="APPLICATION DETAILS" title={loading ? 'Loading…' : application?.companyname || 'Application'} description="Review this opportunity and its interview history." action={<Link to="/applications" className="secondary-button">Back to applications</Link>} /><Notice>{error}</Notice>
    {application && <><section className="panel detail-panel"><div className="detail-top"><span className="company-icon large"><Building2 size={22} /></span><div><div className="eyebrow">{application.companyname}</div><h2>{application.jobname}</h2></div><StatusBadge status={application.status} /></div><div className="detail-facts"><div><span>Applied on</span><b>{dateLabel(application.appliedAt)}</b></div><div><span>Added to ApplyTrack</span><b>{dateLabel(application.createdAt)}</b></div><div><span>Last updated</span><b>{dateLabel(application.updatedAt)}</b></div></div><div className="detail-description"><h3>Job description</h3><p>{application.jobdescription || 'No description added.'}</p></div><div className="detail-actions"><Link to={`/applications/${id}/edit`} className="secondary-button">Edit application</Link><button className="danger-button" onClick={remove}>Delete application</button></div></section><section className="panel related-panel"><div className="panel-heading"><div><h2>Associated interviews</h2><p>Interviews linked to this application.</p></div><Link to={`/interviews/new?applicationId=${id}`} className="secondary-button"><Plus size={15} /> Add interview</Link></div>{interviews.length ? <div className="related-list">{interviews.map((interview) => <Link key={interview.id} to={`/interviews/${interview.id}`} className="related-item"><span className="related-icon"><CalendarDays size={17} /></span><span><b>{interview.interviewRound}</b><small>{dateLabel(interview.scheduledAt, { dateStyle: 'medium', timeStyle: 'short' })}</small></span><StatusBadge status={interview.outcome} /><ArrowRight size={15} /></Link>)}</div> : <div className="empty-inline compact-empty">No interviews linked to this application yet.</div>}</section></>}
  </>;
}

function InterviewsPage() {
  const [interviews, setInterviews] = useState([]);
  const [upcomingOnly, setUpcomingOnly] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [reloadKey, setReloadKey] = useState(0);
  useEffect(() => {
    let alive = true; setLoading(true); setError('');
    (upcomingOnly ? interviewApi.upcoming() : interviewApi.list()).then(({ data }) => { if (alive) setInterviews(data || []); })
      .catch((exception) => { if (alive) setError(errorMessage(exception)); }).finally(() => { if (alive) setLoading(false); });
    return () => { alive = false; };
  }, [upcomingOnly, reloadKey]);
  async function remove(interview) {
    if (!window.confirm(`Delete the ${interview.interviewRound} interview at ${interview.jobApplication?.companyname || 'this company'}?`)) return;
    try { await interviewApi.remove(interview.id); setReloadKey((key) => key + 1); }
    catch (exception) { setError(errorMessage(exception)); }
  }
  return <><PageHeading eyebrow="MAKE EVERY CONVERSATION COUNT" title="Interviews" description="See what’s coming up and keep your conversations in one place." action={<Link to="/interviews/new" className="primary-button"><Plus size={17} /> Schedule interview</Link>} /><Notice>{error}</Notice>
    <section className="panel applications-panel"><div className="toolbar interview-toolbar"><div className="view-tabs"><button className={!upcomingOnly ? 'selected' : ''} onClick={() => setUpcomingOnly(false)}>All interviews</button><button className={upcomingOnly ? 'selected' : ''} onClick={() => setUpcomingOnly(true)}>Upcoming</button></div><span className="result-count">{interviews.length} {interviews.length === 1 ? 'interview' : 'interviews'}</span></div>
      {loading ? <div className="table-state">Loading interviews…</div> : interviews.length ? <div className="table-scroll"><table><thead><tr><th>COMPANY / ROLE</th><th>ROUND</th><th>DATE & TIME</th><th>OUTCOME</th><th>ACTIONS</th></tr></thead><tbody>{interviews.map((interview) => <tr key={interview.id}><td><div className="company-cell"><span className="company-icon"><Building2 size={16} /></span><span><b>{interview.jobApplication?.companyname || 'Company'}</b><small>{interview.jobApplication?.jobname || 'Job application'}</small></span></div></td><td>{interview.interviewRound}</td><td className="muted-cell">{dateLabel(interview.scheduledAt, { dateStyle: 'medium', timeStyle: 'short' })}</td><td><StatusBadge status={interview.outcome} /></td><td><div className="table-actions"><Link to={`/interviews/${interview.id}`} className="row-link">View</Link><Link to={`/interviews/${interview.id}/edit`} className="row-link">Edit</Link><button className="row-delete" onClick={() => remove(interview)} title="Delete interview" aria-label="Delete interview"><X size={15} /></button></div></td></tr>)}</tbody></table></div> : <div className="table-state"><div className="empty-icon"><CalendarDays size={20} /></div><b>{upcomingOnly ? 'No upcoming interviews' : 'No interviews yet'}</b><span>{upcomingOnly ? 'Future scheduled interviews will appear here.' : 'Schedule an interview to keep the details handy.'}</span>{!upcomingOnly && <Link to="/interviews/new" className="text-link">Schedule interview <ArrowRight size={14} /></Link>}</div>}
    </section>
  </>;
}

function InterviewFormPage() {
  const { id } = useParams();
  const editing = Boolean(id);
  const location = useLocation();
  const navigate = useNavigate();
  const queryApplicationId = new URLSearchParams(location.search).get('applicationId') || '';
  const [applications, setApplications] = useState([]);
  const [form, setForm] = useState({ applicationId: queryApplicationId, interviewRound: '', scheduledAt: '', meetingLink: '', outcome: 'SCHEDULED', feedback: '' });
  const [loading, setLoading] = useState(editing);
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);
  useEffect(() => {
    let alive = true;
    Promise.all([
      applicationApi.list({ page: 0, size: 100, sort: 'id,desc' }),
      editing ? interviewApi.details(id) : Promise.resolve(null),
    ]).then(([appResponse, interviewResponse]) => {
      if (!alive) return;
      setApplications(appResponse.data.content || []);
      if (interviewResponse) {
        const interview = interviewResponse.data;
        setForm({ applicationId: String(interview.jobApplication?.id || ''), interviewRound: interview.interviewRound || '', scheduledAt: localDateTimeValue(interview.scheduledAt), meetingLink: interview.meetingLink || '', outcome: interview.outcome || 'SCHEDULED', feedback: interview.feedback || '' });
      }
    }).catch((exception) => { if (alive) setError(errorMessage(exception)); }).finally(() => { if (alive) setLoading(false); });
    return () => { alive = false; };
  }, [editing, id]);
  async function submit(event) {
    event.preventDefault(); setError(''); setBusy(true);
    const payload = { interviewRound: form.interviewRound.trim(), scheduledAt: form.scheduledAt, meetingLink: form.meetingLink.trim() || null, outcome: form.outcome, feedback: form.feedback.trim() || null };
    try {
      if (editing) await interviewApi.update(id, payload);
      else await interviewApi.create(Number(form.applicationId), payload);
      navigate(editing ? `/interviews/${id}` : '/interviews');
    } catch (exception) { setError(errorMessage(exception)); }
    finally { setBusy(false); }
  }
  return <><PageHeading eyebrow={editing ? 'UPDATE THE DETAILS' : 'PLAN AHEAD'} title={editing ? 'Edit interview' : 'Schedule interview'} description={editing ? 'Keep the interview information up to date.' : 'Add the details you have so you can focus on the conversation.'} />
    <section className="form-panel panel">{loading ? <div className="table-state">Loading interview…</div> : <><Notice>{error}</Notice><form className="data-form" onSubmit={submit}><div className="form-grid"><label className="full-field">Job application <span className="required">*</span><select required disabled={editing} value={form.applicationId} onChange={(e) => setForm({ ...form, applicationId: e.target.value })}><option value="">Select an application</option>{applications.map((application) => <option key={application.id} value={application.id}>{application.companyname} — {application.jobname}</option>)}</select></label><label>Interview round <span className="required">*</span><input required value={form.interviewRound} onChange={(e) => setForm({ ...form, interviewRound: e.target.value })} placeholder="e.g. Technical round" /></label><label>Date & time <span className="required">*</span><input type="datetime-local" required value={form.scheduledAt} onChange={(e) => setForm({ ...form, scheduledAt: e.target.value })} /></label><label className="full-field">Meeting link <span className="optional">Optional</span><input type="url" value={form.meetingLink} onChange={(e) => setForm({ ...form, meetingLink: e.target.value })} placeholder="https://…" /></label><label>Outcome<select value={form.outcome} onChange={(e) => setForm({ ...form, outcome: e.target.value })}>{OUTCOMES.map((outcome) => <option key={outcome} value={outcome}>{pretty(outcome)}</option>)}</select></label><label>Feedback<textarea rows={3} value={form.feedback} onChange={(e) => setForm({ ...form, feedback: e.target.value })} placeholder="Notes after the interview…" /></label></div><div className="form-actions"><button type="button" className="secondary-button" onClick={() => navigate(-1)}>Cancel</button><button className="primary-button" disabled={busy || (!editing && !applications.length)}>{busy ? 'Saving…' : editing ? 'Save changes' : 'Schedule interview'}<ArrowRight size={15} /></button></div></form>{!applications.length && !loading && <p className="inline-hint">Add a job application before scheduling an interview.</p>}</>}</section>
  </>;
}

function InterviewDetailsPage() {
  const { id } = useParams();
  const [interview, setInterview] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  useEffect(() => {
    let alive = true;
    interviewApi.details(id).then(({ data }) => { if (alive) setInterview(data); }).catch((exception) => { if (alive) setError(errorMessage(exception)); }).finally(() => { if (alive) setLoading(false); });
    return () => { alive = false; };
  }, [id]);
  async function changeOutcome(outcome) {
    try { const { data } = await interviewApi.updateOutcome(id, outcome); setInterview(data); }
    catch (exception) { setError(errorMessage(exception)); }
  }
  async function remove() {
    if (!window.confirm('Delete this interview?')) return;
    try { await interviewApi.remove(id); navigate('/interviews'); }
    catch (exception) { setError(errorMessage(exception)); }
  }
  return <><PageHeading eyebrow="INTERVIEW DETAILS" title={loading ? 'Loading…' : interview?.interviewRound || 'Interview'} description="Review the plan and capture how the conversation went." action={<Link to="/interviews" className="secondary-button">Back to interviews</Link>} /><Notice>{error}</Notice>
    {interview && <section className="panel detail-panel"><div className="detail-top"><span className="company-icon large"><CalendarDays size={22} /></span><div><div className="eyebrow">{interview.jobApplication?.companyname || 'Company'}</div><h2>{interview.jobApplication?.jobname || interview.interviewRound}</h2></div><StatusBadge status={interview.outcome} /></div><div className="detail-facts"><div><span>Interview round</span><b>{interview.interviewRound}</b></div><div><span>Date & time</span><b>{dateLabel(interview.scheduledAt, { dateStyle: 'medium', timeStyle: 'short' })}</b></div><div><span>Outcome</span><select className="detail-outcome" value={interview.outcome} onChange={(e) => changeOutcome(e.target.value)}>{OUTCOMES.map((outcome) => <option key={outcome} value={outcome}>{pretty(outcome)}</option>)}</select></div></div><div className="detail-description"><h3>Feedback</h3><p>{interview.feedback || 'No feedback recorded.'}</p></div><div className="detail-actions">{interview.meetingLink && <a className="primary-button" href={interview.meetingLink} target="_blank" rel="noreferrer">Join interview <ArrowUpRight size={15} /></a>}<Link className="secondary-button" to={`/interviews/${id}/edit`}>Edit details</Link><button className="danger-button" onClick={remove}>Delete interview</button></div></section>}
  </>;
}

export default App;
