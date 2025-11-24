document.addEventListener('DOMContentLoaded', () => {
    // Seleciona todos os botões de voto no documento
    const voteButtons = document.querySelectorAll('.vote-action');

    voteButtons.forEach(button => {
        button.addEventListener('click', async (event) => {
            event.preventDefault(); // Impede o recarregamento padrão da página

            const url = button.href;
            const card = button.closest('.post-card'); // Encontra o card de postagem mais próximo
            const scoreElement = card ? card.querySelector('.post-sidebar .score') : null;

            if (!scoreElement) {
                console.error("Elemento de pontuação não encontrado.");
                return;
            }
            
            // 1. Envia a requisição para o Servlet /votar
            try {
                const response = await fetch(url, {
                    method: 'GET', // O VotoServlet aceita GET
                    headers: {
                        // Header para indicar ao Servlet que a requisição é AJAX
                        'X-Requested-With': 'XMLHttpRequest' 
                    }
                });

                if (response.ok) {
                    // 2. Tenta obter o novo score (Se o Servlet for modificado para retornar o score)
                    // Como seu Servlet atualmente redireciona, faremos uma atualização SIMPLES:
                    
                    // Lógica SIMPLES: Apenas assume sucesso e incrementa/decrementa na tela.
                    const direcao = parseInt(new URL(url).searchParams.get('direcao'));
                    let currentScore = parseInt(scoreElement.textContent) || 0;
                    
                    // Exemplo: Se o usuário votar, atualizamos. (O VotoServlet deve garantir a lógica complexa de duplo-clique)
                    scoreElement.textContent = currentScore + direcao; 
                    
                    // *Melhoria UX*: Adiciona uma classe temporária para feedback visual
                    scoreElement.classList.add(direcao > 0 ? 'score-up' : 'score-down');
                    setTimeout(() => {
                        scoreElement.classList.remove('score-up', 'score-down');
                    }, 500);

                } else if (response.status === 401 || response.status === 403) {
                    // Se o Servlet retornar Não Autorizado (401), redireciona para o login
                    alert('Você precisa estar logado para votar.');
                    window.location.href = './login.jsp'; 
                } else {
                    alert('Falha ao processar voto.');
                }
            } catch (error) {
                console.error('Erro de rede:', error);
            }
        });
    });
});