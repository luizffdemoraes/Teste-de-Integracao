package br.com.caelum.pm73.dao;

import static org.junit.Assert.*;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.pm73.dominio.Usuario;

public class UsuarioDaoTest {
	
	private Session session;
	private UsuarioDao usuarioDao;

	@Before
	public void antes() {
		session = new CriadorDeSessao().getSession();
		usuarioDao = new UsuarioDao(session);
	}
	
	@After
    public void depois() {
        session.close();
    }

	// Teste de Integração
	// Sempre que temos classes cuja tarefa de comunicar com sistema externo
	// não faz sentido fazer uso de mocks e necessario bater no sistema de verdade
	// Testamos a integração da nossa classe com sistema externo
	@Test
	public void deveEncontrarPeloNomeEEmailMockado() {
		// Estamos usando o HSQLDB BANCO DE DADOS EM MEMÓRIA
		//Session session = new CriadorDeSessao().getSession();
		//UsuarioDao usuarioDao = new UsuarioDao(session);

		Usuario novoUsuario = new Usuario("João da Silva", "joao@dasilva.com.br");
		usuarioDao.salvar(novoUsuario);

		Usuario usuario = usuarioDao.porNomeEEmail("João da Silva", "joao@dasilva.com.br");

		assertEquals("João da Silva", usuario.getNome());
		assertEquals("joao@dasilva.com.br", usuario.getEmail());

		//session.close();

	}

	@Test
	public void deveRetornarNuloSeNaoEncontrarUsuario() {
		Session session = new CriadorDeSessao().getSession();
		UsuarioDao usuarioDao = new UsuarioDao(session);

		Usuario usuarioDoBanco = usuarioDao.porNomeEEmail("João Joaquim", "joao@joaquim.com.br");

		assertNull(usuarioDoBanco);

		session.close();
	}

	// Não faz sentido utilizar mock para classe Dao. Pois a classse Dao acessa o
	// banco de dados se a unica
	// coisa que o Mock faz e sminular o banco
//	@Test
//	public void deveEncontrarPeloNomeEEmailMockado() {
//		Session session = Mockito.mock(Session.class);
//		Query query = Mockito.mock(Query.class);
//		UsuarioDao usuarioDao = new UsuarioDao(session);
//
//		Usuario usuario = new Usuario("João da Silva", "joao@dasilva.com.br");
//		String sql = "from Usuario u where u.nome = :nome and u.email = :email";
//
//		Mockito.when(session.createQuery(sql)).thenReturn(query);
//		Mockito.when(query.uniqueResult()).thenReturn(usuario);
//		Mockito.when(query.setParameter("nome", "João da Silva")).thenReturn(query);
//		Mockito.when(query.setParameter("email", "joao@dasilva.com.br")).thenReturn(query);
//
//		Usuario usuarioDoBanco = usuarioDao.porNomeEEmail("João da Silva", "joao@dasilva.com.br");
//
//		assertEquals(usuario.getNome(), usuarioDoBanco.getNome());
//		assertEquals(usuario.getEmail(), usuarioDoBanco.getEmail());
//    
//	
//	}

}
