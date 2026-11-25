import React, { useEffect, useState } from 'react'
import { createRoot } from 'react-dom/client'
import { approveNote, createNote, getNotes } from './api.js'
import './style.css'

const families = [1, 2]
const dateFormatter = new Intl.DateTimeFormat(undefined, { dateStyle: 'medium' })

function Dashboard({ familyId }) {
  const [notes, setNotes] = useState(null)
  const [loadError, setLoadError] = useState('')
  const [attempt, retry] = useState(0)
  const [actionError, setActionError] = useState('')
  const [notice, setNotice] = useState('')
  const [pending, setPending] = useState('')
  const [family, setFamily] = useState(1)
  const [title, setTitle] = useState('')
  const [content, setContent] = useState('')
  const tutor = familyId === null

  useEffect(() => {
    const controller = new AbortController()
    getNotes(familyId, controller.signal).then((result) => {
      if (controller.signal.aborted) return
      setNotes(tutor ? result : result.filter((note) => note.familyId === familyId))
    }).catch((error) => {
      if (!controller.signal.aborted) setLoadError(error.message)
    })
    return () => controller.abort()
  }, [familyId, tutor, attempt])

  async function save(event) {
    event.preventDefault()
    if (!title.trim() || !content.trim() || pending) return
    setPending('create')
    setActionError('')
    setNotice('')
    try {
      const note = await createNote({ familyId: family, title: title.trim(), content: content.trim() })
      setNotes((current) => [note, ...current])
      setTitle('')
      setContent('')
      setNotice('Lesson note saved.')
    } catch (error) {
      setActionError('Could not save the note. ' + error.message)
    } finally {
      setPending('')
    }
  }

  async function approve(note) {
    if (pending) return
    setPending(note.id)
    setActionError('')
    setNotice('')
    try {
      const updated = await approveNote(note.id)
      setNotes((current) => current.map((item) => item.id === updated.id ? updated : item))
      setNotice('Lesson note approved.')
    } catch (error) {
      setActionError('Could not approve the note. ' + error.message)
    } finally {
      setPending('')
    }
  }

  return (
    <main id="main" className="shell">
      <div className="intro">
        <h1>{tutor ? 'Tutor dashboard' : 'Family ' + familyId + ' dashboard'}</h1>
        <p>{tutor ? 'Create lesson summaries and review them for approval.' : 'Review the lesson notes for your family.'}</p>
      </div>
      {actionError && <p className="feedback error" role="alert">{actionError}</p>}
      {notice && <p className="feedback" role="status">{notice}</p>}
      <div className={tutor ? 'dashboard tutor' : 'dashboard'}>
        {tutor && (
          <section className="panel compose" aria-labelledby="compose-title">
            <h2 id="compose-title">New lesson note</h2>
            <form onSubmit={save}>
              <label>Family
                <select value={family} onChange={(event) => setFamily(Number(event.target.value))} disabled={pending === 'create'}>
                  {families.map((id) => <option key={id} value={id}>Family {id}</option>)}
                </select>
              </label>
              <label>Lesson title
                <input required value={title} onChange={(event) => setTitle(event.target.value)} placeholder="e.g. Introduction to fractions" disabled={pending === 'create'} />
              </label>
              <label>Lesson content
                <textarea required value={content} onChange={(event) => setContent(event.target.value)} placeholder="Summarize what you covered in the lesson." rows={6} disabled={pending === 'create'} />
              </label>
              <button className="primary" disabled={!notes || Boolean(pending) || !title.trim() || !content.trim()}>
                {pending === 'create' ? 'Saving…' : 'Save lesson note'}
              </button>
            </form>
          </section>
        )}
        <section className="panel library" aria-labelledby="notes-title">
          <div className="section-heading">
            <h2 id="notes-title">Lesson notes</h2>
            {notes && <span className="count">{notes.length} {notes.length === 1 ? 'note' : 'notes'}</span>}
          </div>
          {loadError ? (
            <div className="state">
              <p role="alert">{loadError}</p>
              <button onClick={() => { setLoadError(''); retry((value) => value + 1) }}>Try again</button>
            </div>
          ) : !notes ? (
            <p className="state" role="status">Loading lesson notes…</p>
          ) : notes.length === 0 ? (
            <div className="state"><h3>No lesson notes yet</h3><p>{tutor ? 'Use the form to add your first note.' : 'Notes for your family will appear here.'}</p></div>
          ) : (
            <div className="notes">
              {notes.map((note) => (
                <article className="note" key={note.id}>
                  <div className="note-heading">
                    <h3>{note.title}</h3>
                    {tutor && <span className={note.approved ? 'badge approved' : 'badge'}>{note.approved ? 'Approved' : 'Pending approval'}</span>}
                  </div>
                  <p className="meta">
                    {tutor && <>Family {note.familyId} · </>}
                    <time dateTime={note.createdAt}>{dateFormatter.format(new Date(note.createdAt))}</time>
                  </p>
                  <p className="content">{note.content}</p>
                  {tutor && !note.approved && (
                    <button disabled={Boolean(pending)} onClick={() => approve(note)} aria-label={'Approve lesson note: ' + note.title}>
                      {pending === note.id ? 'Approving…' : 'Approve note'}
                    </button>
                  )}
                </article>
              ))}
            </div>
          )}
        </section>
      </div>
    </main>
  )
}

function App() {
  const [familyId, setFamilyId] = useState(null)
  return (
    <>
      <a className="skip" href="#main">Skip to dashboard</a>
      <header>
        <div className="shell topbar">
          <div className="brand"><span className="mark" aria-hidden="true">T</span> TutorMe</div>
          <nav aria-label="Dashboard">
            {[null, ...families].map((id) => (
              <button key={id ?? 'tutor'} aria-pressed={familyId === id} onClick={() => setFamilyId(id)}>
                {id === null ? 'Tutor' : 'Family ' + id}
              </button>
            ))}
          </nav>
        </div>
      </header>
      <Dashboard key={familyId ?? 'tutor'} familyId={familyId} />
    </>
  )
}

createRoot(document.getElementById('root')).render(<App />)
