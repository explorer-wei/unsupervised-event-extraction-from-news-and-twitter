package clustering;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import model.Document;
import constants.ClusterConstants;
import constants.GeneralConstants;
import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class KMeansCluster {
	/**
	 * Do clustering to the specified documents
	 * 
	 * @param docList
	 * @param featureSet
	 * @return
	 */
	public static int[] clusterDocuments(final List<Document> docList,
			final Set<String> featureSet) {
		int[] instanceClusterIDs;
		List<Document> instanceList = new ArrayList<Document>();
		for (Document doc : docList) {
			instanceList.add(doc.clone());
		}

		String instanceCSV = GeneralConstants.WORKING_DIRECTORY
				+ File.separator + "instances.csv";
		exportFeatureToCSV(instanceList, instanceCSV, featureSet);

		SimpleKMeans clusterer = new SimpleKMeans();
		DataSource source;
		Instances data;

		String optionStr = "-N "
				+ ClusterConstants.CLUSTER_NUM
				+ " -A \"weka.core.EuclideanDistance -R first-last\" -I 500 -S 10";
		String[] options;

		try {
			// Create the clusterer
			source = new DataSource(instanceCSV);
			data = source.getDataSet();
			options = weka.core.Utils.splitOptions(optionStr);
			clusterer.setOptions(options);
			clusterer.buildClusterer(data);

			// Clustering every instance
			Instance[] instances = new Instance[instanceList.size()];
			instanceClusterIDs = new int[instanceList.size()];
			for (int i = 0; i < instances.length; i++) {
				instances[i] = data.instance(i);
				instanceClusterIDs[i] = clusterer.clusterInstance(instances[i]);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return instanceClusterIDs;
	}

	public static void exportFeatureToCSV(List<Document> docList, String url,
			final Set<String> featureSet) {
		File file = new File(url);
		String[] featureArray = new String[featureSet.size()];
		featureArray = featureSet.toArray(featureArray);

		try {
			PrintWriter pw = new PrintWriter(file);

			int docNum = 0;
			for (Document doc : docList) {
				// Print the CSV head
				if (docNum == 0) {
					int termNum = 0;
					for (String key : featureArray) {
						pw.print(key);
						// Do not print comma at the end of the line
						if (termNum < featureArray.length - 1)
							pw.print(",");
						termNum++;
					}
					pw.println();
				}

				// Print every row (for every document)
				int termNum = 0;
				int[] tfs = doc.getTermFrequencies();
				for (int i = 0; i < tfs.length; i++) {
					int value = tfs[i];
					pw.print(value);
					// Do not print comma at the end of the line
					if (termNum < tfs.length - 1)
						pw.print(",");
					termNum++;
				}
				pw.println();
				docNum++;
			}
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
