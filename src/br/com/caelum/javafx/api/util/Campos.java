package br.com.caelum.javafx.api.util;

import static br.com.caelum.javafx.api.controllers.JavaFXUtil.DEU_PAU_EXCEPTION;
import static br.com.caelum.javafx.api.controllers.JavaFXUtil.mostraAlerta;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import br.com.caelum.javafx.api.annotations.EhAtributoDaConta;
import br.com.caelum.javafx.api.controllers.Controller;
import br.com.caelum.javafx.api.modelo.Campo;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class Campos {
	
	private Map<String, Campo> todosOsCampos = new HashMap<>();

	public void registraCampos(Controller controller){
		Field[] atributos = controller.getClass().getDeclaredFields();
		for (Field atributo : atributos) {
			if(atributo.getType().isAssignableFrom(TextField.class) && atributo.isAnnotationPresent(FXML.class)){
				try {
					atributo.setAccessible(true);
					String nomeDoAtributo = atributo.getName();
					Campo campo = new Campo(atributo.isAnnotationPresent(EhAtributoDaConta.class), (TextField) atributo.get(controller), nomeDoAtributo);
					todosOsCampos.put(nomeDoAtributo, campo);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					mostraAlerta(DEU_PAU_EXCEPTION);
					throw new RuntimeException(e);
				}
			}
		}
	}
	
	public TextField buscaCampoDeTexto(String campo) {
		return buscaCampo(campo);
	}

	public ToggleGroup buscaSelecionado(String campo) {
		return buscaCampo(campo);
	}
	
	@SuppressWarnings("unchecked")
	private <T> T buscaCampo(String campo) {
		Object encontrado = todosOsCampos.get(campo).getValor();
		
		if(encontrado != null) {
			T campoDeTexto = (T) encontrado;
			return campoDeTexto;
		}
		
		throw new RuntimeException("Não foi encontrado o campo com o nome " + campo + ". Verifique se o nome está correto.");
	}
	
	public List<String> getNomeDosCampos(){
		return todosOsCampos.values().stream().filter(campo -> campo.ehAtributoDaConta()).map(campo -> campo.getNome()).collect(Collectors.toList());
	}
}
