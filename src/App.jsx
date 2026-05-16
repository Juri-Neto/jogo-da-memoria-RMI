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
  const [currentScreen, setCurrentScreen] = useState('home')

  if (currentScreen === 'mode') {
    return (
      <main className="screen">
        <section className="mode-panel" aria-label="Escolha do modo de jogo">
          {questionMarks.map((item) => (
            <span className={item.className} key={item.className}>
              {item.symbol}
            </span>
          ))}

          <header className="mode-header">
            <button
              className="back-button"
              type="button"
              onClick={() => setCurrentScreen('home')}
              aria-label="Voltar para a tela inicial"
            >
              ‹
            </button>

            <div className="mode-title">
              <p>Como você</p>
              <h1>Quer jogar?</h1>
              <span>Escolha o modo que mais combina com você!</span>
            </div>
          </header>

          <div className="mode-options">
            <button className="mode-card online-card" type="button" onClick={() => setCurrentScreen('desktop')}>
              <span className="mode-illustration" aria-hidden="true">
                🌎
              </span>

              <span className="mode-copy">
                <strong>Jogar Online</strong>
                <span>Use o cliente desktop Java RMI para jogar com outra pessoa.</span>
              </span>

              <span className="mode-arrow" aria-hidden="true">
                ›
              </span>
            </button>

            <button className="mode-card local-card" type="button">
              <span className="mode-illustration" aria-hidden="true">
                👦🏻👧🏻
              </span>

              <span className="mode-copy">
                <strong>Jogar Local</strong>
                <span>Jogue com amigos ou familiares no mesmo dispositivo!</span>
              </span>

              <span className="mode-arrow" aria-hidden="true">
                ›
              </span>
            </button>
          </div>
        </section>
      </main>
    )
  }

  if (currentScreen === 'desktop') {
    return (
      <main className="screen">
        <section className="mode-panel" aria-label="Informações do cliente desktop">
          {questionMarks.map((item) => (
            <span className={item.className} key={item.className}>
              {item.symbol}
            </span>
          ))}

          <header className="mode-header">
            <button
              className="back-button"
              type="button"
              onClick={() => setCurrentScreen('mode')}
              aria-label="Voltar para os modos de jogo"
            >
              ‹
            </button>

            <div className="mode-title">
              <p>Jogar Online</p>
              <h1>Cliente Java Swing</h1>
              <span>Use o projeto `rmi-client` para se conectar ao servidor RMI e jogar com outro desktop.</span>
            </div>
          </header>

          <div className="mode-options">
            <div className="mode-card local-card" style={{ cursor: 'default' }}>
              <span className="mode-copy">
                <strong>Passos</strong>
                <span>1. Inicie `rmi-server`. 2. Execute `rmi-client`. 3. Crie ou entre em uma sala.</span>
              </span>
            </div>
          </div>
        </section>
      </main>
    )
  }

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
              <button
                className="play-button"
                type="button"
                onClick={() => setCurrentScreen('mode')}
              >
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
