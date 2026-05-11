import { useState } from 'react'
import brainImg from './assets/brain.png'

const questionMarks = [
  { symbol: '?', className: 'mark mark-one' },
  { symbol: '?', className: 'mark mark-two' },
  { symbol: '?', className: 'mark mark-three' },
  { symbol: '?', className: 'mark mark-four' },
  { symbol: '?', className: 'mark mark-five' },
  { symbol: '*', className: 'mark sparkle-one' },
  { symbol: ' ', className: 'mark sparkle-two' },
]

function App() {
  const [musicOn, setMusicOn] = useState(true)

  return (
    <main className="screen">
      <section className="home-panel" aria-label="Tela inicial do jogo">
        {questionMarks.map((item) => (
          <span className={item.className} key={item.className}>
            {item.symbol}
          </span>
        ))}

        <div className="title-area">
          <p className="title-white">Jogo da</p>
          <h1>Memória</h1>
        </div>

        <div className="content-row">
          <img
            src={brainImg}
            alt="Cérebro"
            className="brain-image"
          />

          <div className="menu-area">
            <p className="subtitle">Encontre todos os pares!</p>

            <div className="actions">
              <button className="play-button" type="button">
                <span className="play-icon" aria-hidden="true"></span>
                Jogar
              </button>

              <button className="rules-button" type="button">
                <span aria-hidden="true">🏆</span>
                Regras
              </button>
            </div>

            <button
              className={musicOn ? 'music-button active' : 'music-button'}
              type="button"
              aria-label={musicOn ? 'Desligar musica' : 'Ligar musica'}
              onClick={() => setMusicOn((current) => !current)}
            >
              ♪
            </button>
          </div>
        </div>
      </section>
    </main>
  )
}

export default App
