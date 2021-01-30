package console;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jgibblda.Estimator;
import jgibblda.Inferencer;
import jgibblda.LDACmdOption;
import jgibblda.Model;
import model.Document;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import constants.ClusterConstants;
import clustering.*;
import preprocess.DocumentProcess;
import LDA.LDAUtility;

/**
 * The main program to extract customer opinion via LDA
 * 
 * Program Arguments: -est -ntopics 5 -twords 5 -dir {LDA objective directory}
 * -dfile {LDA input file name} Working Directory: {LDA objective directory}
 * 
 * @author Xuan Zhang
 * 
 */
public class LDAConsole {
	public static List<Document>[] clusterTitles(String titleFileUrl){
		List<Document>[] array = new List[ClusterConstants.CLUSTER_NUM];
		for(int i=0;i<array.length;i++){
			array[i]  = new ArrayList<Document>();
		}

		List<Document> documentList = DocumentProcess.createDocumentsFromNewsTitles(titleFileUrl);
		Set<String> termSet = DocumentProcess.getPopularTermSet(documentList);
		System.err.println("\nNo. Of features: " + termSet.size() + "\n");
		DocumentProcess.createTermFrequencies(documentList, termSet);
		
//		int[] ids = KMeansCluster.clusterDocuments(documentList, termSet);
		int[] ids = EMCluster.clusterDocuments(documentList, termSet);
		
		//Add each document to the corresponding list. Every cluster has one list
		for(int i=0;i<ids.length;i++){
			int clusterID = ids[i];
			array[clusterID].add(documentList.get(i));
		}
		
		int num = 0;
		for(List<Document> list : array){
			System.err.println("Cluster [" + num + "]:" + list.size() + " documents");
			num++;
		}
		
		return array;
	}
	
	public static void applyLDA(String LDARootDir, List<Document>[] docListArray){
		int num=0;
		for(List<Document> list : docListArray){
			try {
				
				String ldaDir = LDARootDir + File.separator + num;
				
				String[] args = {"-est", "-ntopics", "10", "-twords", "10", "-dir", ldaDir, "-dfile","LDA.txt"};
				LDACmdOption option = new LDACmdOption();
				CmdLineParser parser = new CmdLineParser(option);
				parser.parseArgument(args);
				String ldaFileUrl = ldaDir + File.separator + option.dfile;
				
				//Produce the file needed by LDA
				LDAUtility.produceLDAFileFromClusteredTitles(list, ldaFileUrl);
				
				//Run LDA
				if (option.est || option.estc) {
					Estimator estimator = new Estimator();
					estimator.init(option);
					estimator.estimate();
				} else if (option.inf) {
					Inferencer inferencer = new Inferencer();
					inferencer.init(option);

					Model newModel = inferencer.inference();

					for (int i = 0; i < newModel.phi.length; ++i) {
						// phi: K * V
						System.out.println("-----------------------\ntopic" + i
								+ " : ");
						for (int j = 0; j < 10; ++j) {
							System.out.println(inferencer.globalDict.id2word.get(j)
									+ "\t" + newModel.phi[i][j]);
						}
					}
				}
				
				LDAUtility.translateLDAResult(ldaDir + File.separator
						+ "model-final.twords", ldaDir + File.separator
						+ "result.txt", Integer.parseInt(args[4]));
				
				num++;
			} catch (CmdLineException e) {
				e.printStackTrace();
				continue;
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
/*		LDACmdOption option = new LDACmdOption();
		CmdLineParser parser = new CmdLineParser(option);

		try {
			if (args.length == 0) {
				LDAUtility.showHelp(parser);
				return;
			}

			parser.parseArgument(args);

			// String textRootDir = "D:\\AppleNews";
			String titleFileUrl = "D:\\Projects\\EventExtraction\\subject.out";

			// createLDAInput("D:\\Ddisk\\pos_after_Bi\\",option.dfile);
			// LDAUtility.produceLDAFile("D:\\Data\\Hisence_TV\\neg_seg_bi",
			// option.dfile);
			// LDAUtility.produceLDAFile(textRootDir, option.dir +
			// File.separator
			// + option.dfile);
			// LDAUtility.produceLDAFileFromAPPReview(option.dir +
			// File.separator
			// + option.dfile);
			// LDAUtility.produceLDAFileFromTitles(textRootDir, option.dir +
			// File.separator
			// + option.dfile);
			LDAUtility.produceLDAFileFromTitleFile(titleFileUrl, option.dir
					+ File.separator + option.dfile);

			if (option.est || option.estc) {
				Estimator estimator = new Estimator();
				estimator.init(option);
				estimator.estimate();
			} else if (option.inf) {
				Inferencer inferencer = new Inferencer();
				inferencer.init(option);

				Model newModel = inferencer.inference();

				for (int i = 0; i < newModel.phi.length; ++i) {
					// phi: K * V
					System.out.println("-----------------------\ntopic" + i
							+ " : ");
					for (int j = 0; j < 10; ++j) {
						System.out.println(inferencer.globalDict.id2word.get(j)
								+ "\t" + newModel.phi[i][j]);
					}
				}
			}
		} catch (CmdLineException cle) {
			System.out.println("Command line error: " + cle.getMessage());
			LDAUtility.showHelp(parser);
			return;
		} catch (Exception e) {
			System.out.println("Error in main: " + e.getMessage());
			e.printStackTrace();
			return;
		}

		// readLDAResult("D:\\text5.txt",option.dir+File.separator+"model-final.twords");//"D:\\lda\\model-final.twords");
		LDAUtility.translateLDAResult(option.dir + File.separator
				+ "model-final.twords", option.dir + File.separator
				+ "result.txt", Integer.parseInt(args[4]));*/
		String titleFileDir = "D:\\LDA\\subject.out";
		String LDARootDir = "D:\\LDA";
		
		List<Document>[] array = clusterTitles(titleFileDir);
		applyLDA(LDARootDir, array);
	}

}
