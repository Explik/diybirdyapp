# The Orb Weaver Learning Algorithm Specification

## Overview
TODO : Write overview

## Introduction
The orb weaver algorithm is a spaced-repetition learning algorithm based on the idea of a spider web. Each piece of content is a juicy fly on the web. The learner crawls the web while balancing 3 competing priorities:
1. Immobilizing new flies 
2. Reinforcing the web around already caught flies as they loosen over time  
3. Reinforcing the web around partcicularly problematic flies before they destroy the web

### Beyond the forgetting curve 
Most spaced-repetion algorithms rely on estimating an appropiate delay period between reviews. This approach is nearly always based on the idea of the forgetting curve, which models the decay of memory retention over time. By interupting this decay with reviews, the forgetting curve model aims to optimize the timing of reviews to maximize retention while minimizing the number of reviews needed. In most algorithms (based on the forgetting curve), the next appropiate time for a review of an item is based on the performance in previous reviews for the same item. This approach assumes two things. (1) The retention of an item can be modelled separately from the other items. (2) The retention can be modelled based solely on the data available to the algorithm. These assumptions are at odds with the pricinples of this system, because this system allows 

The orb weaver algorithm solves these inherent issue by replacing estimation with testing. Testing is enabled by the data model, which handles all content as interconnected nodes on a graph. This interconnected web of content allows "retention difficulties" to ripple through. For example, let's say you ask someone to recall the danish translation of "to be" and you give them 4 options: "at være", "at gå", "vare", "at blive". If they pick incorrectly, you know they have not fully retained the translation "to be" - "at være". However, you can also infer that they also don't fully know the translations of the options as well, because they would most likely have mentally have excluded those options if they knew. This idea of inferring lack of retention can be expanded to include, lack of retetion of spelling in erronous pronunciation, lack of retention of grammatical gender in grammatical error, so on. Modelling every error type would be too complex, so instead, the orb weaver algorithm just prioritizes exercising regions of content that have a high error rate, regardless of the error type. To facilitate this, the learner must be given the oppurtinity to fail. 

### Failing fast 
A lot of language learning apps try to avoid failures by increasing the difficulty slowly. This gives a feeling of compentency and progress, but it also leads to slow progression and less effective learning. The orb weaver algorithm takes another approach instead of avoiding failure it encourages it by quickly ramping up the difficulty and then on failure it backs off and slowly builds up. In practice, this leads to the following exercise flow: 

1. Review flashcard ✅
2. Select flashcard ✅
3. Write flashcard ❌
4. Write flashcard ❌
5. Select individual parts of flashcard ✅
6. Write individual parts of flashcard ✅
7. Write flashcard ✅

This flow allows the system to quickly identify areas of difficulty, address these with additional practice and then move on. All the learner has to do is fail once in a while, so failure must never be punishing or discouraged in any way.

### Summary
The orb weaver learning algorithm is based on two interconnected principles: 
1. Testing and failing fast
2. Prioritizing content based on direct or indirect failures
