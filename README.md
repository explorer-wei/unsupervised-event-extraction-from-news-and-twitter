# Unsupervised Event Extraction from News and Twitter
 
## Overview

This project explores the approaches to extract key events from newswires and Twitter data in an unsupervised manner, where the Topic Modeling and the Named Entity Recognition have been applied. Various methods have been tried regarding the different traits of news and tweets. The relevance between the news events and the corresponding Twitter events is studied as well. Tools have been developed to implement and evaluate these methods. Our experiments show that these tools can effectively extract key events from the news and tweets data sets.

## Architecture

### News Event Extractor

![Architecture of the News Event Extractor](/Documents/news-event-extractor-architecture.png)

The architecture of the ***News Event Extractor*** is demonstrated by the figure above. Two important functions have been implemented, the ***event extraction***, and the ***event similarity calculation***.

To extract events from news articles, first, the text analysis (tokenization, stemming, stop-word removing, etc) is performed to the imported news text by the Text Analyzer, which is implemented based on the [Stanford NLP](http://www-nlp.stanford.edu/software/corenlp.shtml). After that, topics are extracted by the LDA module, which relies on the [Mallet tool](http://mallet.cs.umass.edu/). Since a topic, a word array with usually 7~10 words, is too short to extract named entities, we need to find named entities (i.e. people, location, and time) which are related with the event from the news paragraphs which are closely relevant with the topic. Thus, the [Apache Lucene](http://lucene.apache.org/core/) is used to index all the news paragraphs, and to search the paragraphs which are relevant with a particular topic. The named entities (who, where, and when) are extracted from these paragraphs by the NER module, which has applied the [Stanford NER tool](http://www-nlp.stanford.edu/software/CRF-NER.shtml). These entities are categorized into 3 types and ranked by term frequency, in order to find key entities regarding an event. In addition, the important named entity combinations are also identified to help users understand the event. The [FP-Growth algorithm](https://github.com/BigPeng/FPtree) is utilized to identify these combinations. Finally, an “Event Creator” component produces events based on the extracted topics and corresponding named entities.

In term of the event similarity model, the [Google Word2Vec tool](https://code.google.com/archive/p/word2vec/) has been used to produce a vector set for every event. The distance between two events is measured by a similarity model, which calculates the distance between the centroids of two spheres formed by the two vector sets, in a hyper dimension space.

![Event Similarity Measured by Word Vectors](/Documents/news-event-extractors-compare.png)

Some components like the database, [weka clustering](https://www.cs.waikato.ac.nz/ml/weka/), and HTML parser offer fundamental functions to the system.

### Twitter Event Extractor

Same as the pipeline of the News Event Extractor, the Twitter Event Extractor can also divided into four steps: ***Text Analyzer, LDA, NER and Event Creator***. 

- Text Analyzer: the text analysis (tokenization, stemming, stop-word removing, etc) is performed to the tweets’ text by the Text Analyzer, which is implemented based on the Stanford NLP. 
- LDA: the LDA component is to perform topic modeling process for tweets content. The Python library we used is [Gensim LDA component](http://radimrehurek.com/gensim/index.html). Firstly, we use LDA component to create the LDA model by the whole tweets content. Then, for each tweet, we use the model we built to label topic to each tweet. 
- NER: The NER component is for the Name Entities Recognition (NER). In this part, we used [PyNER python library](https://github.com/dat/pyner) as the interface of Stanford NER service.  Our NER parsing process used 7 classes name entities. They include Time, Location, Organization, Person, Money, Percent, and Date. 
- Event Creator: after we got the NER results and topic labels of each tweet, we can generate the event. For each event, it contains 7 keywords from LDA topics, “When” component and “Where” component. We get “When” component from “Person” and “Organization” labels of NER result and “Where” component from “Location” labels of NER result. 

![News Events on a Time Line](/Documents/news-event-extractor-output.png)

## Acknowledge
Thanks to Xuan Zhang, Ji Wang, and Tianyu Geng for working with me in "CS6604 Digital Libraries" @ Virginia Tech 2014 Spring.
