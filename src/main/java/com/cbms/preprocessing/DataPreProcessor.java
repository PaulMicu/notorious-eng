/**
 * Interface for the data pre-processor
 * the Process function will reduce the data and store it in the reducedDataSet variable 
 * 
 * @author      Paul Micu
 * @version     1.0
 * @last_edit   11/01/2020
 */
package com.cbms.preprocessing;

import weka.core.Instances;

public interface DataPreProcessor {
    Instances reducedDataset = null;
    
    public void process() throws Exception;
    public Instances getReducedDataset();
}
