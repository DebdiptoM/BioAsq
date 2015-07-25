#!/usr/bin/python
try:
    import json
except ImportError:
    import simplejson as json
#import json
#import nltk
import re
import string
import os.path
import sys
from pprint import pprint
#from nltk.corpus import stopwords
#from nltk import word_tokenize, wordpunct_tokenize
# def split_line(text):

#     # split the text
#     words = text.split()

#     # for each word in the line:
#     for word in words:

#         # prints each word on a line
#         # print(word)
def write_vocab(filename,hash_array):
    filehandle=open(filename,"a")
    keysinarray=hash_array.keys()
    #print keysinarray[0:]
    for wkey in keysinarray:
        filehandle.write(wkey+"\n")
    filehandle.close()

def filetohash(filename):
    filehandle=open(filename,"r")
    hash_array=dict()
    for i in filehandle:
        hash_array[i.strip()]=0;
    filehandle.close()
    return hash_array

def stripstopwords_punctuations(t1):
    punctuations = '''!()-[]{}1234567890;:'"\,<>./?@#$%^&*_~'''
    # 
    text = t1.lower()
#    text = ' '.join([word for word in text.split() if word not in (nltk.corpus.stopwords.words('english'))])
    text = ' '.join([word for word in text.split() if word not in punctuations])
    text=text.encode('ascii','replace')
    text=text.translate(None,punctuations)
    return text
    # print text
    # text.strip(punctuations)
    # from nltk.tokenize import RegexpTokenizer

    # tokenizer = RegexpTokenizer(r'\w')
    # t2=tokenizer.tokenize(text)

    # print t2

    # exclude = string.punctuation#['?','.','#','(',')','']
    # s = ''.join([text for text in text if text not in exclude])
    # s=split_line(s)
def tokenize_words(text):
    
    s = nltk.word_tokenize(text)
    d={}
    for token in s:
        if  d.has_key(token):
            d[token]=d[token]+1
        else:
            d[token]=1
            if(not hash_arr.has_key(token)):
                filehandle_wc=open("vocab.txt","a")
                filehandle_wc.write(token+"\n")
                filehandle_wc.close()
    return d
# Main Program
#file='/Users/Deb/Downloads/BioASQ-SampleDataA.json-2.txt'
file=sys.argv[1]
try:
#	with open(file,"r") as data_file:    
		data_file=open(file,"r") 
   		data = json.load(data_file)
		data_file.close()
except IOError:
	print "Error file not found"
	
nObjects=len(data['articles'])
print nObjects
#Existing vocab
if(os.path.exists('vocab.txt')):
    hash_arr=filetohash('vocab.txt')
else:
    hash_arr={}
 #Loading files
stopwords=filetohash('stopwords.txt')
object_hash_tokenized=[]
for fileno in  range(nObjects):

    # pprint(data['articles'][0]['pmid'])
    # pprint(data['articles'][0]['abstractText'])
    # pprint(data['articles'][0]['meshMajor'])
    
    t1 =data['articles'][fileno]['abstractText']
    print fileno
    tokenized_hash=dict()
    tokenized=re.split('\W+', t1.lower())
    for token in tokenized:
    	if(stopwords.has_key(token)):
	   tokenized.remove(token)
	else:
	   tokenized_hash[token]=0

	
    	#tokenized=re.split('\W+', t1.lower())
    
    print tokenized_hash
	
    #object_hash_tokenized.extend(tokenize_words(t1))
    # print d
    # write_vocab('vocab.txt',t1_hash_arr)
    # hash_arr=filetohash('vocab.txt')
    #pprint (object_hash_tokenized)
    # TODO:Duplicate hash entry 

