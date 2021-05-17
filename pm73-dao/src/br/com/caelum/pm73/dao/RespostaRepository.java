package br.com.caelum.pm73.dao;

import java.util.Collection;

import javax.persistence.EntityManager;

public class RespostaRepository {

	private EntityManager manager;
//
//	public RespostaRepository(EntityManager manager) {
//		this.manager = manager;
//	}
//
//	public Collection<Resposta> buscaRespostas(Long idAluno) {
//		return manager.createQuery("select r from Resposta r where r.aluno.id =    :idAluno", Resposta.class)
//				.setParameter("idAluno", idAluno).getResultList();
//	}
//
//	
//    @Test(expected = IllegalArgumentException.class)
//    public void naoDeveriaAceitaNotaMaiorQueDez(){
//        RespostaQuestao resposta = new RespostaQuestao(11);
//        Assert.fail();
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void naoDeveriaAceitaNotaMenorQueZero(){
//        RespostaQuestao resposta = new RespostaQuestao(-1);
//        Assert.fail();
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void naoDeveriaAceitaNotaNula(){
//        RespostaQuestao resposta = new RespostaQuestao(null);
//        Assert.fail();
//    }

}
