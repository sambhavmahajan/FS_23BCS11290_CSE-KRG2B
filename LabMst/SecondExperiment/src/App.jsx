import { useState } from 'react'

function App() {
  const [formData, setFormData] = useState({ name: '', email: '', course: '' })
  const [submissions, setSubmissions] = useState([])

  const handleSubmit = (e) => {
    e.preventDefault()
    setSubmissions([...submissions, formData])
    setFormData({ name: '', email: '', course: '' })
  }

  return (
    <div className="p-4">
      <h1 className="text-2xl mb-4">Student Form</h1>
      
      <form onSubmit={handleSubmit} className="mb-4">
        <input
          placeholder="Name"
          value={formData.name}
          onChange={(e) => setFormData({...formData, name: e.target.value})}
          className="border p-1 mr-2"
          required
        />
        <input
          type="email"
          placeholder="Email"
          value={formData.email}
          onChange={(e) => setFormData({...formData, email: e.target.value})}
          className="border p-1 mr-2"
          required
        />
        <input
          placeholder="Course"
          value={formData.course}
          onChange={(e) => setFormData({...formData, course: e.target.value})}
          className="border p-1 mr-2"
          required
        />
        <button type="submit" className="bg-blue-500 text-white p-1">Submit</button>
      </form>

      {submissions.length > 0 && (
        <table className="border">
          <thead>
            <tr>
              <th className="border p-2">Name</th>
              <th className="border p-2">Email</th>
              <th className="border p-2">Course</th>
            </tr>
          </thead>
          <tbody>
            {submissions.map((item, index) => (
              <tr key={index}>
                <td className="border p-2">{item.name}</td>
                <td className="border p-2">{item.email}</td>
                <td className="border p-2">{item.course}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}

export default App