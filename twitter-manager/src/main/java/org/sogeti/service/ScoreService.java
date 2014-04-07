package org.sogeti.service;


public class ScoreService {
	
	
	public static boolean isScoreOk(String description){
		
		long scoreOk = Integer.parseInt(TwitterService.PROP.getScoreOk());
		if(getScore(description) >= scoreOk){
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * Calcule un score pour un friend/following en fonction de la recherche de mot clé dans la description.
	 * @param userBean
	 * @return
	 */
	public static long getScore(String description) {
		long score = 0;
		description = description.toLowerCase();
		//Calcul du score sur mots de niveau 1
		score = rechercehMotsCle(1, score, TwitterService.PROP.getCriterian1(), TwitterService.PROP.getCriterian1conditions(), description);
		
		//Calcul du score sur mots de niveau 2
		score = rechercehMotsCle(2, score, TwitterService.PROP.getCriterian2(), TwitterService.PROP.getCriterian2conditions(), description);
				
		//Calcul du score sur mots de niveau 3
		score = rechercehMotsCle(3, score, TwitterService.PROP.getCriterian3(), TwitterService.PROP.getCriterian3conditions(), description);
		
		return score;
	}
	
	private static long rechercehMotsCle(int poids, long score, String criteres, String criteresCondition, String description){
		String[] tokens = criteres.split(",");
		int tokensFound = 0;
		for (String token : tokens) {
			if(description.contains(token)){
				tokensFound++;
			}
		}
		//Calcul du score sur mots cle avec condition
		if(!criteresCondition.isEmpty()){
			String[] criteriaListeCondition = criteresCondition.split("#");
			for (String condition : criteriaListeCondition) {
				String[] listeMots = condition.split(";");
				String[] mots1 = listeMots[0].split(",");
				String[] mots2 = listeMots[1].split(",");
				for (String mot1 : mots1) {
					if(description.contains(mot1)){
						for (String mot2 : mots2) {
							if(description.contains(mot2)){
								tokensFound++;
							}
						}
					}
				}
			}		
		}
		score = score + tokensFound*poids;
		return score;
	}
}
