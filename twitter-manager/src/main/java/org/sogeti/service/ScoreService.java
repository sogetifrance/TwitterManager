package org.sogeti.service;


public class ScoreService {
	
	
	public static boolean isScoreOk(String description){
		long scoreOk = Integer.parseInt(TwitterService.PROP.getProperty("scoreOk"));
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
		score = rechercehMotsCle(1, score, "criterian1", "criterian1conditions", description);
		
		//Calcul du score sur mots de niveau 2
		score = rechercehMotsCle(2, score, "criterian2", "criterian2conditions", description);
				
		//Calcul du score sur mots de niveau 3
		score = rechercehMotsCle(3, score, "criterian3", "criterian3conditions", description);
		
		return score;
	}
	
	private static long rechercehMotsCle(int poids, long score, String criteres, String criteresCondition, String description){
		String criteria = TwitterService.PROP.getProperty(criteres).toLowerCase();
		String[] tokens = criteria.split(",");
		int tokensFound = 0;
		for (String token : tokens) {
			if(description.contains(token)){
				tokensFound++;
			}
		}
		//Calcul du score sur mots cle avec condition
		criteria = TwitterService.PROP.getProperty(criteresCondition).toLowerCase();
		if(!criteria.isEmpty()){
			String[] criteriaListeCondition = criteria.split("#");
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
