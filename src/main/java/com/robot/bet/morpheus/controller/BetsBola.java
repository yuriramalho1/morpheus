package com.robot.bet.morpheus.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.robot.bet.morpheus.entity.Aposta;
import com.robot.bet.morpheus.model.enumeration.StatusAposta;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BetsBola {
	
	private static String urlBet = "https://www.mixbet.com.br/";

    public void runBet(List<Aposta> apostas) {
    	
    	
    	apostas.forEach(a->{
    		a.setValorSaldo(retornaSaldo(a));
    	});
    	
    	BigDecimal valorBrutoAposta = this.calcularValorBrutoAposta(apostas);
    	if(valorBrutoAposta.equals(BigDecimal.ZERO)) {
    		apostas.forEach(a->{
            	a.setStatusAposta(StatusAposta.SEM_SALDO);
            	a.setMessageError("Usuário sem Saldo");
    		});
    		return;
    	}
    	
    	if(!this.calcularValorAposta(apostas, valorBrutoAposta)) {
    		apostas.forEach(a->{
            	a.setStatusAposta(StatusAposta.COTACAO_FALHOU);
            	a.setMessageError("A cotação não foi calculada corretamente");
    		});
    		return;
    	}
    	
    	for(Aposta aposta: apostas) {
    		
	    	String absolutePath = System.getenv("PATH_FIREFOX_DRIVER");
	    	System.setProperty("webdriver.gecko.driver", absolutePath);
	        WebDriver driver = new FirefoxDriver();
	        
	    	try {
	    		
	    		//Aposta aposta = new Aposta();
	    		//aposta.setTimeCasa("Flamengo");
	    		//aposta.setTimeFora("Palmeiras");
	    		//aposta.setIdCampeonato(1352L);
	    		//aposta.setIdPartida(1247705L);
	    		//aposta.setIdOpcao(85678737L);
	    		//Calendar dataEvento = Calendar.getInstance();
	    		//dataEvento.set(Calendar.DAY_OF_MONTH, 20);
	    		//dataEvento.set(Calendar.HOUR_OF_DAY, 19);
	    		//dataEvento.set(Calendar.MINUTE, 30);
	    		//dataEvento.set(Calendar.SECOND, 0);
	    		//dataEvento.set(Calendar.MILLISECOND, 0);
	    		//aposta.setDataEvento(dataEvento.getTime());
	    		
	            driver.get(urlBet);
	
	            WebElement campoUser = (new WebDriverWait(driver, 120)).until(ExpectedConditions.presenceOfElementLocated(By.name("txtUser")));
	            campoUser.clear();
	            campoUser.sendKeys(aposta.getUsuario().getLogin());
	
	            WebElement campoPassword = driver.findElement(By.name("txtPwd"));
	            campoPassword.clear();
	            campoPassword.sendKeys(aposta.getUsuario().getSenha());
	            
	            WebElement botaoEntrar = (new WebDriverWait(driver, 120)).until(ExpectedConditions.elementToBeClickable(By.id("loguese")));
	            
				(new WebDriverWait(driver, 20)).until(new ExpectedCondition<Boolean>() {
					public Boolean apply(WebDriver objDriver) {
						WebElement processando = objDriver.findElement(By.id("divProcessando"));
						return processando.getCssValue("display").contentEquals("none");
					}
				});
	            
	            botaoEntrar.click();
	
				(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
					public Boolean apply(WebDriver objDriver) {
						WebElement processando = objDriver.findElement(By.id("divProcessando"));
						return processando.getCssValue("display").contentEquals("none");
					}
				});
				
				
	            WebElement gradeCampeonato = (new WebDriverWait(driver, 20)).until(ExpectedConditions.presenceOfElementLocated(By.id("grdCamp")));
	            
	            BigDecimal valorAposta = aposta.getValorAposta();
	            
	            List<WebElement> campeonatos = gradeCampeonato.findElements(By.tagName("a"));
	            
	            //a tag é encontrada, mas ainda sem ser preenchida
	            //foi necessário criar esse processo para aguardar
	            int tentativa = 1;
	            while(campeonatos.size()==0) {
	            	
	            	try {
	        			(new WebDriverWait(driver, 5)).until(new ExpectedCondition<Boolean>() {
	        				public Boolean apply(WebDriver objDriver) {
	        					WebElement processando = objDriver.findElement(By.id("divProcessando"));
	        					return !processando.getCssValue("display").contentEquals("none");
	        				}
	        			});
					} catch (Exception e) {
						// TODO: handle exception
					}
	            	
	            	campeonatos = gradeCampeonato.findElements(By.tagName("a"));
	            	if(tentativa==10) {
	            		break;
	            	}
	            	tentativa++;
	            }
	            
	            boolean entrouNoCampeonato = false;
	            if(campeonatos.size()>0) {
	            	for(WebElement c: campeonatos)
	            		if(c.getAttribute("href").equals("javascript:ConsultarJogos("+aposta.getIdCampeonato()+")")) {
	            			c.click();
	            			entrouNoCampeonato = true;
	            			break;
	           		}
	            }
	            
	            if(entrouNoCampeonato) {
	    			(new WebDriverWait(driver, 20)).until(new ExpectedCondition<Boolean>() {
	    				public Boolean apply(WebDriver objDriver) {
	    					WebElement processando = objDriver.findElement(By.id("divProcessando"));
	    					return processando.getCssValue("display").contentEquals("none");
	    				}
	    			});
	    			
	    			
	                WebElement gradeJogos = (new WebDriverWait(driver, 20)).until(ExpectedConditions.presenceOfElementLocated(By.id("grdJogos")));
	                
	                List<WebElement> jogos = gradeJogos.findElements(By.tagName("tr"));
	                tentativa = 1;
	                while(jogos.size()==0) {
	                	
	                	try {
	            			(new WebDriverWait(driver, 5)).until(new ExpectedCondition<Boolean>() {
	            				public Boolean apply(WebDriver objDriver) {
	            					WebElement processando = objDriver.findElement(By.id("divProcessando"));
	            					return !processando.getCssValue("display").contentEquals("none");
	            				}
	            			});
	    				} catch (Exception e) {
	    					// TODO: handle exception
	    				}
	                	
	                	jogos = gradeJogos.findElements(By.tagName("tr"));
	                	if(tentativa==10) {
	                		break;
	                	}
	                	tentativa++;
	                }
	                
	                if(jogos.size()>0) {
	                	boolean encontrou = false;
	                	for(WebElement j: jogos) {
	                		List<WebElement> opcoesGrade = j.findElements(By.tagName("a"));
	                		if(opcoesGrade.size()>0) {
	                			for(WebElement o: opcoesGrade) {
	                				
	                        		if(o.getAttribute("href").contains("javascript:MaisOdds("+aposta.getIdPartida())) {
	                        			o.click();
	                        			encontrou = true;
	                        			break;
	                        		}
	                        		
	                			}
	                    		if(encontrou) {
	                    			break;
	                    		}
	                		}
	                	}
	
	            		if(encontrou) {
		        			(new WebDriverWait(driver, 20)).until(new ExpectedCondition<Boolean>() {
		        				public Boolean apply(WebDriver objDriver) {
	            					WebElement processando = objDriver.findElement(By.className("modalWindow2"));
	            					return processando.getCssValue("display").contentEquals("block");
		        				}
		        			});
		        			
		                    WebElement modalOdds = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.className("modalWindow2")));
		                    
		                    List<WebElement> odds = modalOdds.findElements(By.tagName("a"));
		                    tentativa = 1;
		                    while(odds.size()==0) {
		                    	
		                    	try {
		                			(new WebDriverWait(driver, 5)).until(new ExpectedCondition<Boolean>() {
		                				public Boolean apply(WebDriver objDriver) {
		                					WebElement processando = objDriver.findElement(By.id("divProcessando"));
		                					return !processando.getCssValue("display").contentEquals("none");
		                				}
		                			});
		        				} catch (Exception e) {
		        					// TODO: handle exception
		        				}
		                    	
		                    	odds = modalOdds.findElements(By.tagName("a"));
		                    	if(tentativa==10) {
		                    		break;
		                    	}
		                    	tentativa++;
		                    }
		                    
		                    boolean encontrouOdd = false;
		                    if(odds.size()>0) {
		                    	for(WebElement o: odds) {
	                        		if(o.getAttribute("href").equals("javascript:CheckOdd2Local("+aposta.getIdPartida()+", "+aposta.getIdOpcao()+");")) {
	                        			o.click();
	                        			encontrouOdd = true;
	                        			break;
	                        		}
		                    	}
		                    }
		                    
		                    if(encontrouOdd) {
		                    	try {
		                			(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
		                				public Boolean apply(WebDriver objDriver) {
		                					WebElement processando = objDriver.findElement(By.id("modalWindow2"));
		                					return processando.getCssValue("display").contentEquals("none");
		                				}
		                			});
		        				} catch (Exception e) {
		        					// TODO: handle exception
		        				}
		                    	
		                        WebElement campoValor = driver.findElement(By.id("EditVlAposta"));
		                        campoValor.clear();
		                        campoValor.sendKeys(valorAposta.toString());
		                        
		                        WebElement botaoConfirmar = driver.findElement(By.id("btConfirmar"));
		                        botaoConfirmar.click();
		                        
		                        new WebDriverWait(driver, 30000);

		                        //habilitar isso quando for para produção
		                        try {
			                        Alert alert = driver.switchTo().alert();
			                        alert.accept();
			                        alert.sendKeys(Keys.ENTER.toString());
								} catch (Exception e) {
									// TODO: handle exception
									/*driver.quit();
									
									e.printStackTrace();
						        	aposta.setStatusAposta(StatusAposta.FALHOU);
						        	aposta.setMessageError(e.getMessage());
						        	continue;*/
								}
		                        
		                    	aposta.setStatusAposta(StatusAposta.PROCESSADA);
		                    }
		        			
		        			
	            		}
	                
	                }
	                
	            }
	            
	            
	            driver.quit();
	            
			} catch (Exception e) {
				// TODO: handle exception
				driver.quit();
				
				e.printStackTrace();
	        	aposta.setStatusAposta(StatusAposta.FALHOU);
	        	aposta.setMessageError(e.getMessage());
				
			}
    	}
    	
    	this.enviarNotificacaoApostasComFalha(apostas);
    	
	}
    
    private BigDecimal retornaSaldo(Aposta aposta) {
    	BigDecimal valorSaldo = BigDecimal.ZERO;

    	
    	
    	String absolutePath = System.getenv("PATH_FIREFOX_DRIVER");
    	System.setProperty("webdriver.gecko.driver", absolutePath);
        WebDriver driver = new FirefoxDriver();
        
    	try {
    		
            driver.get(urlBet);

            WebElement campoUser = (new WebDriverWait(driver, 120)).until(ExpectedConditions.presenceOfElementLocated(By.name("txtUser")));
            campoUser.clear();
            campoUser.sendKeys(aposta.getUsuario().getLogin());

            WebElement campoPassword = driver.findElement(By.name("txtPwd"));
            campoPassword.clear();
            campoPassword.sendKeys(aposta.getUsuario().getSenha());
            
            WebElement botaoEntrar = (new WebDriverWait(driver, 120)).until(ExpectedConditions.elementToBeClickable(By.id("loguese")));
            
			(new WebDriverWait(driver, 20)).until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver objDriver) {
					WebElement processando = objDriver.findElement(By.id("divProcessando"));
					return processando.getCssValue("display").contentEquals("none");
				}
			});
            
            botaoEntrar.click();

			(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver objDriver) {
					WebElement processando = objDriver.findElement(By.id("divProcessando"));
					return processando.getCssValue("display").contentEquals("none");
				}
			});
			
			
            WebElement lblSaldo = (new WebDriverWait(driver, 20)).until(ExpectedConditions.presenceOfElementLocated(By.id("lbSaldoUser")));
            try {
                valorSaldo = new BigDecimal(Double.valueOf(lblSaldo.getText().replace("$", "").trim()));
			} catch (Exception e) {
				e.printStackTrace();
            	aposta.setStatusAposta(StatusAposta.FALHOU);
            	aposta.setMessageError(e.getMessage());
				return BigDecimal.ZERO;
			}
            
            if(valorSaldo==null || valorSaldo.compareTo(BigDecimal.ZERO)<1) {
            	System.out.println("Usuário ("+aposta.getUsuario().getId()+"-"+aposta.getUsuario().getLogin()+") Não possui Saldo");
            	aposta.setStatusAposta(StatusAposta.SEM_SALDO);
            	aposta.setMessageError("Usuário sem Saldo");
				return BigDecimal.ZERO;
            }
            
            driver.quit();
            
		} catch (Exception e) {
			// TODO: handle exception
			driver.quit();
			
			e.printStackTrace();
        	aposta.setStatusAposta(StatusAposta.FALHOU);
        	aposta.setMessageError(e.getMessage());
			
		}
    	
    	return valorSaldo;
    }
    
    public BigDecimal calcularValorBrutoAposta(List<Aposta> apostas) {
    	
    	BigDecimal valorAposta = BigDecimal.ZERO;
    	for(Aposta a: apostas) {
    		if(a.getValorSaldo().equals(BigDecimal.ZERO)) {
    			return BigDecimal.ZERO;
    		}
            BigDecimal valor = a.getValorSaldo().multiply(new BigDecimal(10.00)).divide(new BigDecimal(100.00), RoundingMode.DOWN);
            if(!valorAposta.equals(BigDecimal.ZERO) && valorAposta.compareTo(valor)>0) {
            	valorAposta = valor;
            } else if (valorAposta.equals(BigDecimal.ZERO)) {
            	valorAposta = valor;
            }
            
            if(valor.compareTo(new BigDecimal(100.00))<0) {
    			return BigDecimal.ZERO;
            }
    	}
    	
    	return valorAposta;
    }
    
    public boolean calcularValorAposta(List<Aposta> apostas, BigDecimal valorBrutoAposta) {
    	/*
    	 * 

verdadeiro calculo

pega as cotações 

verifica o percentual de diferença entre elas

100 - ((Menor * 100) / Maior) = diferença (%)

dividir a diferença por 2

diferenca(%) / 2 = metade da diferença (esse será o valor responsável por somar com a metade do total para ser apostado)

pegar o valor que será responsável por somar com a metade do aportado e diminuir com metade do aportado: 
metade_aportado = (total_aportado / 2)
((metade_aportado) + ((metade_aportado * metade_da_diferenca(%))/100)) * menor_cotacao
((metade_aportado) - ((metade_aportado * metade_da_diferenca(%))/100)) * maior_cotacao

lembrando que a diferença entre os dois tem que ser maior que 1%

**************exemplo de aposta***************

aporte = 1000,00

acima de 9  = 2,11
abaixo de 9 = 2,15

100 - ((2,11*100)/2,15) = 1,86 (diferença %)

metade: 1,86/2 = 0,93 (%)

metade_aportado: 1000,00 / 2 = 500,00

((500) + ((500*0,93)/100)) * 2,11 = 1064,81
((500) - ((500*0,93)/100)) * 2,15 = 1065,00

 
    	 */
    	Aposta aposta1 = apostas.get(0);
    	Aposta aposta2 = apostas.get(1);
    	
    	BigDecimal cotacaoMenor    = BigDecimal.ZERO;
    	BigDecimal cotacaoMaior    = BigDecimal.ZERO;
    	BigDecimal apostaMenor     = BigDecimal.ZERO;
    	BigDecimal apostaMaior     = BigDecimal.ZERO;
    	BigDecimal diferenca       = BigDecimal.ZERO;
    	BigDecimal metadeDiferenca = BigDecimal.ZERO;
    	BigDecimal metadeBruto     = BigDecimal.ZERO;
    	
    	cotacaoMenor = aposta1.getCotacao();
    	cotacaoMaior = aposta2.getCotacao();
    	boolean invertido = false;
    	if(cotacaoMenor.compareTo(cotacaoMaior)>0) {
        	cotacaoMenor = aposta2.getCotacao();
        	cotacaoMaior = aposta1.getCotacao();
        	invertido = true;
    	}
    	
    	diferenca       = (new BigDecimal(100.00)).subtract(cotacaoMenor.multiply(new BigDecimal(100.00)).divide(cotacaoMaior, 2, RoundingMode.HALF_DOWN));
    	metadeDiferenca = diferenca.divide(new BigDecimal(2.00), 2, RoundingMode.HALF_DOWN);
    	metadeBruto     = valorBrutoAposta.divide(new BigDecimal(2.00), 2, RoundingMode.HALF_DOWN);
    	
    	apostaMenor = metadeBruto.add(metadeBruto.multiply(metadeDiferenca).divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN));
    	apostaMaior = metadeBruto.subtract(metadeBruto.multiply(metadeDiferenca).divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN));
    	
    	if(apostaMenor.multiply(cotacaoMenor).compareTo(valorBrutoAposta)<0 ||
    	   apostaMaior.multiply(cotacaoMaior).compareTo(valorBrutoAposta)<0) {
    		return false;
    	}

    	aposta1.setValorAposta(apostaMenor.setScale(0, RoundingMode.HALF_DOWN));
    	aposta2.setValorAposta(apostaMaior.setScale(0, RoundingMode.HALF_DOWN));
    	if(invertido) {
        	aposta1.setValorAposta(apostaMaior);
        	aposta2.setValorAposta(apostaMenor);
    	}
    	
    	return true;
    }
	
	public void enviarNotificacaoApostasComFalha(List<Aposta> apostas) {
		for(Aposta aposta: apostas) {
			if(aposta.getStatusAposta().equals(StatusAposta.PENDENTE) || 
				aposta.getStatusAposta().equals(StatusAposta.COTACAO_FALHOU) ||
				aposta.getStatusAposta().equals(StatusAposta.SEM_SALDO) ||
				aposta.getStatusAposta().equals(StatusAposta.FALHOU)) {
				//enviar email
				
				OkHttpClient client = new OkHttpClient().newBuilder().build();
				MediaType mediaType = MediaType.parse("application/json");
				RequestBody body = RequestBody.create(mediaType, ""
				+ "{\"nomeBot\":\""+9+"\",\r\n    \"numeros\":\""+"83996705554"+"\", "
				+ "\"corpoMensagem\":\"🚨  Morpheus - ALERTA DE APOSTA QUE FALHOU  🚨  "
			    + "Aposta: "+aposta.toString()+" "
				+"   🤖\"}");
				Request request = new Request.Builder()
				  .url("http://191.252.182.184:3000/enviarMsg")
				  .method("POST", body)
				  .addHeader("Content-Type", "application/json")
				  .build();
				try (Response response = client.newCall(request).execute()) {
					//client.newCall(request).execute();
					if(!response.isSuccessful()) {
						System.out.println(response.message());
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
		}
	}
	
}
