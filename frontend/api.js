const baseUrl = 'http://localhost:8080/api'

async function request(path, options = {}) {
  let response
  try {
    response = await fetch(baseUrl + path, {
      ...options,
      headers: {
        Accept: 'application/json',
        ...(options.body && { 'Content-Type': 'application/json' }),
      },
    })
  } catch (error) {
    if (error.name === 'AbortError') throw error
    throw new Error('Could not reach TutorMe. Check that the backend is running on port 8080.')
  }
  if (response.status === 404) throw new Error('The requested resource could not be found.')
  if (!response.ok) throw new Error('The server could not complete the request (HTTP ' + response.status + ').')
  return response.json()
}

export const getNotes = (familyId, signal) =>
  request(familyId ? '/families/' + familyId + '/notes' : '/notes', { signal })
export const createNote = (note) =>
  request('/notes', { method: 'POST', body: JSON.stringify(note) })
export const approveNote = (id) =>
  request('/notes/' + encodeURIComponent(id) + '/approve', { method: 'PUT' })
