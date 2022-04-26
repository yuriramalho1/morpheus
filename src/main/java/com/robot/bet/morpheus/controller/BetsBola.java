package com.robot.bet.morpheus.controller;

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

public class BetsBola {
	
	

    public static void runBet(Aposta aposta) {
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
    		
            driver.get("http://www.mixbet.com.br/");

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
            
            WebElement lblSaldo = (new WebDriverWait(driver, 20)).until(ExpectedConditions.presenceOfElementLocated(By.id("lbSaldoUser")));
            BigDecimal valorSaldo = BigDecimal.ZERO;
            try {
                valorSaldo = new BigDecimal(Double.valueOf(lblSaldo.getText().replace("$", "").trim()));
			} catch (Exception e) {
				e.printStackTrace();
            	aposta.setStatusAposta(StatusAposta.FALHOU);
            	aposta.setMessageError(e.getMessage());
				return;
			}
            
            if(valorSaldo==null || valorSaldo.compareTo(BigDecimal.ZERO)<1) {
            	System.out.println("Usuário ("+aposta.getUsuario().getId()+"-"+aposta.getUsuario().getLogin()+") Não possui Saldo");
            	aposta.setStatusAposta(StatusAposta.SEM_SALDO);
            	aposta.setMessageError("Usuário sem Saldo");
            	return;
            }
            
            BigDecimal valorAposta = valorSaldo.multiply(new BigDecimal(10.00)).divide(new BigDecimal(100.00), RoundingMode.DOWN);
            
            
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
	                        
	                        try {
		                        Alert alert = driver.switchTo().alert();
		                        alert.accept();
		                        alert.sendKeys(Keys.ENTER.toString());
							} catch (Exception e) {
								// TODO: handle exception
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
	
}
