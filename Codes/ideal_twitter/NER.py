__author__ = 'ericshape'

import nltk

def extract_entity_names(t):
    entity_names = []

    if hasattr(t, 'node') and t.node:
        if t.node == 'NE':
            entity_names.append(' '.join([child[0] for child in t]))
        else:
            for child in t:
                entity_names.extend(extract_entity_names(child))

    return entity_names


with open('news.txt', 'r') as f:
    inputTextFile = f.read()

sentences = nltk.sent_tokenize(inputTextFile)
print sentences
tokenized_sentences = [nltk.word_tokenize(sentence) for sentence in sentences]
tagged_sentences = [nltk.pos_tag(sentence) for sentence in tokenized_sentences]
chunked_sentences = nltk.batch_ne_chunk(tagged_sentences, binary=True)

entity_names = []
for tree in chunked_sentences:
    # Print results per sentence
    #print extract_entity_names(tree)
    entity_names.extend(extract_entity_names(tree))

# Print all entity names
print entity_names

