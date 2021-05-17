package br.com.caelum.pm73.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.List;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.pm73.dominio.Leilao;
import br.com.caelum.pm73.dominio.Usuario;

public class LeilaoDaoTest {

	private Session session;
	private LeilaoDao leilaoDao;
	private UsuarioDao usuarioDao;

	@Before
	public void antes() {
		session = new CriadorDeSessao().getSession();
		leilaoDao = new LeilaoDao(session);
		usuarioDao = new UsuarioDao(session);

		// inicia transacao
		session.beginTransaction();
	}

	@After
	public void depois() {
		// faz o rollback
		session.getTransaction().rollback();
		session.close();
	}

	@Test
	public void deveContarLeiloesNaoEncerrados() {
		// criamos um usuario
		Usuario mauricio = new Usuario("Mauricio Aniche", "mauricio@aniche.com.br");

		// criamos os dois leiloes
		Leilao ativo = new Leilao("Geladeira", 1500.0, mauricio, false);
		Leilao encerrado = new Leilao("XBox", 700.0, mauricio, false);
		encerrado.encerra();

		// persistimos todos no banco
		usuarioDao.salvar(mauricio);
		leilaoDao.salvar(ativo);
		leilaoDao.salvar(encerrado);

		// invocamos a acao que queremos testar
		// pedimos o total para o DAO
		long total = leilaoDao.total();

		assertEquals(1L, total);

	}

	@Test
	public void deveRetornarZeroSeNaoHaLeiloesNovos() {
		Usuario mauricio = new Usuario("Mauricio Aniche", "mauricio@aniche.com.br");

		Leilao encerrado = new Leilao("XBox", 700.0, mauricio, false);
		Leilao tambemEncerrado = new Leilao("Geladeira", 1500.0, mauricio, false);
		encerrado.encerra();
		tambemEncerrado.encerra();

		usuarioDao.salvar(mauricio);
		leilaoDao.salvar(encerrado);
		leilaoDao.salvar(tambemEncerrado);

		long total = leilaoDao.total();

		assertEquals(0L, total);
	}

	@Test
	public void deveRetornarLeiloesDeProdutosNovos() {
		Usuario mauricio = new Usuario("Mauricio Aniche", "mauricio@aniche.com.br");

		Leilao produtoNovo = new Leilao("XBox", 700.0, mauricio, false);
		Leilao produtoUsado = new Leilao("Geladeira", 1500.0, mauricio, true);

		usuarioDao.salvar(mauricio);
		leilaoDao.salvar(produtoNovo);
		leilaoDao.salvar(produtoUsado);

		List<Leilao> novos = leilaoDao.novos();

		assertEquals(1, novos.size());
		assertEquals("XBox", novos.get(0).getNome());
	}

	@Test
	public void deveTrazerSomenteLeiloesAntigos() {
		Usuario mauricio = new Usuario("Mauricio Aniche", "mauricio@aniche.com.br");

		Leilao recente = new Leilao("XBox", 700.0, mauricio, false);
		Leilao antigo = new Leilao("Geladeira", 1500.0, mauricio, true);

		Calendar dataRecente = Calendar.getInstance();
		Calendar dataAntiga = Calendar.getInstance();
		dataAntiga.add(Calendar.DAY_OF_MONTH, -10);

		recente.setDataAbertura(dataRecente);
		antigo.setDataAbertura(dataAntiga);

		usuarioDao.salvar(mauricio);
		leilaoDao.salvar(recente);
		leilaoDao.salvar(antigo);

		List<Leilao> antigos = leilaoDao.antigos();

		assertEquals(1, antigos.size());
		assertEquals("Geladeira", antigos.get(0).getNome());
	}

	@Test
	public void deveTrazerSomenteLeiloesAntigosHaMaisDe7Dias() {
		Usuario mauricio = new Usuario("Mauricio Aniche", "mauricio@aniche.com.br");

		Leilao noLimite = new Leilao("XBox", 700.0, mauricio, false);

		Calendar dataAntiga = Calendar.getInstance();
		dataAntiga.add(Calendar.DAY_OF_MONTH, -7);

		noLimite.setDataAbertura(dataAntiga);

		usuarioDao.salvar(mauricio);
		leilaoDao.salvar(noLimite);

		List<Leilao> antigos = leilaoDao.antigos();

		assertEquals(1, antigos.size());
	}

	@Test
	public void deveTrazerLeiloesNaoEncerradosNoPeriodo() {

		// criando as datas
		Calendar comecoDoIntervalo = Calendar.getInstance();
		comecoDoIntervalo.add(Calendar.DAY_OF_MONTH, -10);
		Calendar fimDoIntervalo = Calendar.getInstance();
		Calendar dataDoLeilao1 = Calendar.getInstance();
		dataDoLeilao1.add(Calendar.DAY_OF_MONTH, -2);
		Calendar dataDoLeilao2 = Calendar.getInstance();
		dataDoLeilao2.add(Calendar.DAY_OF_MONTH, -20);

		Usuario mauricio = new Usuario("Mauricio Aniche", "mauricio@aniche.com.br");

		// criando os leiloes, cada um com uma data
		Leilao leilao1 = new Leilao("XBox", 700.0, mauricio, false);
		leilao1.setDataAbertura(dataDoLeilao1);
		Leilao leilao2 = new Leilao("Geladeira", 1700.0, mauricio, false);
		leilao2.setDataAbertura(dataDoLeilao2);

		// persistindo os objetos no banco
		usuarioDao.salvar(mauricio);
		leilaoDao.salvar(leilao1);
		leilaoDao.salvar(leilao2);

		// invocando o metodo para testar
		List<Leilao> leiloes = leilaoDao.porPeriodo(comecoDoIntervalo, fimDoIntervalo);

		// garantindo que a query funcionou
		assertEquals(1, leiloes.size());
		assertEquals("XBox", leiloes.get(0).getNome());
	}

	@Test
	public void naoDeveTrazerLeiloesEncerradosNoPeriodo() {

		// criando as datas
		Calendar comecoDoIntervalo = Calendar.getInstance();
		comecoDoIntervalo.add(Calendar.DAY_OF_MONTH, -10);
		Calendar fimDoIntervalo = Calendar.getInstance();
		Calendar dataDoLeilao1 = Calendar.getInstance();
		dataDoLeilao1.add(Calendar.DAY_OF_MONTH, -2);

		Usuario mauricio = new Usuario("Mauricio Aniche", "mauricio@aniche.com.br");

		// criando os leiloes, cada um com uma data
		Leilao leilao1 = new Leilao("XBox", 700.0, mauricio, false);
		leilao1.setDataAbertura(dataDoLeilao1);
		leilao1.encerra();

		// persistindo os objetos no banco
		usuarioDao.salvar(mauricio);
		leilaoDao.salvar(leilao1);

		// invocando o metodo para testar
		List<Leilao> leiloes = leilaoDao.porPeriodo(comecoDoIntervalo, fimDoIntervalo);

		// garantindo que a query funcionou
		assertEquals(0, leiloes.size());
	}
	
//    @Test
//    public void deveDeletarUmUsuario() {
//        Usuario usuario = 
//                new Usuario("Mauricio Aniche", "mauricio@aniche.com.br");
//
//        usuarioDao.salvar(usuario);
//        usuarioDao.deletar(usuario);
//
//        Usuario usuarioNoBanco = 
//                usuarioDao.porNomeEEmail("Mauricio Aniche", "mauricio@aniche.com.br");
//
//        assertNull(usuarioNoBanco);
//
//    }
    
    @Test
    public void deveDeletarUmUsuario() {
        Usuario usuario = 
                new Usuario("Mauricio Aniche", "mauricio@aniche.com.br");

        usuarioDao.salvar(usuario);
        usuarioDao.deletar(usuario);

        // envia tudo para o banco de dados        
        session.flush();
        //session.clear();

        Usuario usuarioNoBanco = 
                usuarioDao.porNomeEEmail("Mauricio Aniche", "mauricio@aniche.com.br");

        assertNull(usuarioNoBanco);

    }
    
    @Test
    public void deveAlterarUmUsuario() {
        Usuario usuario = 
                new Usuario("Mauricio Aniche", "mauricio@aniche.com.br");

        usuarioDao.salvar(usuario);

        usuario.setNome("João da Silva");
        usuario.setEmail("joao@silva.com.br");

        usuarioDao.atualizar(usuario);

        session.flush();

        Usuario novoUsuario = 
                usuarioDao.porNomeEEmail("João da Silva", "joao@silva.com.br");
        assertNotNull(novoUsuario);
        System.out.println(novoUsuario);

        Usuario usuarioInexistente = 
                usuarioDao.porNomeEEmail("Mauricio Aniche", "mauricio@aniche.com.br");
        assertNull(usuarioInexistente);

    }

//    @Test
//    public void listaSomenteOsLeiloesDoUsuario() throws Exception {
//        Usuario dono = new Usuario("Mauricio", "m@a.com");
//        Usuario comprador = new Usuario("Victor", "v@v.com");
//        Usuario comprador2 = new Usuario("Guilherme", "g@g.com");
//        Leilao leilao = new LeilaoBuilder()
//            .comDono(dono)
//            .comValor(50.0)
//            .comLance(Calendar.getInstance(), comprador, 100.0)
//            .comLance(Calendar.getInstance(), comprador2, 200.0)
//            .constroi();
//        Leilao leilao2 = new LeilaoBuilder()
//            .comDono(dono)
//            .comValor(250.0)
//            .comLance(Calendar.getInstance(), comprador2, 100.0)
//            .constroi();
//        usuarioDao.salvar(dono);
//        usuarioDao.salvar(comprador);
//        usuarioDao.salvar(comprador2);
//        leilaoDao.salvar(leilao);
//        leilaoDao.salvar(leilao2);
//
//        List<Leilao> leiloes = leilaoDao.listaLeiloesDoUsuario(comprador);
//        assertEquals(1, leiloes.size());
//        assertEquals(leilao, leiloes.get(0));
//    }
    
//    @Test
//    public void listaDeLeiloesDeUmUsuarioNaoTemRepeticao() throws Exception {
//        Usuario dono = new Usuario("Mauricio", "m@a.com");
//        Usuario comprador = new Usuario("Victor", "v@v.com");
//        Leilao leilao = new LeilaoBuilder()
//            .comDono(dono)
//            .comLance(Calendar.getInstance(), comprador, 100.0)
//            .comLance(Calendar.getInstance(), comprador, 200.0)
//            .constroi();
//        usuarioDao.salvar(dono);
//        usuarioDao.salvar(comprador);
//        leilaoDao.salvar(leilao);
//
//        List<Leilao> leiloes = leilaoDao.listaLeiloesDoUsuario(comprador);
//        assertEquals(1, leiloes.size());
//        assertEquals(leilao, leiloes.get(0));
//    }
    
//    @Test
//    public void devolveAMediaDoValorInicialDosLeiloesQueOUsuarioParticipou(){
//        Usuario dono = new Usuario("Mauricio", "m@a.com");
//        Usuario comprador = new Usuario("Victor", "v@v.com");
//        Leilao leilao = new LeilaoBuilder()
//            .comDono(dono)
//            .comValor(50.0)
//            .comLance(Calendar.getInstance(), comprador, 100.0)
//           // .comLance(Calendar.getInstance(), comprador, 200.0)
//            .constroi();
//        Leilao leilao2 = ((LeilaoBuilder) new LeilaoBuilder()
//            .comDono(dono)
//            .comValor(250.0)
//            .comLance(Calendar.getInstance(), comprador, 100.0))
//            .constroi();
//        usuarioDao.salvar(dono);
//        usuarioDao.salvar(comprador);
//        leilaoDao.salvar(leilao);
//        leilaoDao.salvar(leilao2);
//
//        assertEquals(150.0, leilaoDao.getValorInicialMedioDoUsuario(comprador), 0.001);
//    }
    
}
