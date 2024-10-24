Assumptions and constraints are all embedded within the code as comments at the appropriate places.

Below is only a summary / collation of these:

1. For exercising / testing the code, external Data source feed has been simulated with a data pump which is currently restricted to a bounded data set. This can be easily extended to an ever running unbounded data pump.
2. timestamps have been modelled as long primitives for efficiency and performance reasons
2. Assumption has been made that the timestamp in the TRADE correlates 1:1 with the corresponding QUOTE timestamp used for making the trade. 
    Removing the above assumption would mean we store the incoming quote data in a sorted data structure like TreeMap where it is efficient to search for top quote that is closest to the timestamp on the trade. The quote closest imply the one that matches the trade price and has a timestamp nearest to the trade. Again this presents more questions as to what if two quotes within a time window has the same price as trade price but having a different counterpart price (bid vs ask). So in this case, what if the actual quote that was used actually hasn't arrived yet, and we landed up using an old one that has matching trade price but different counterpart price. Hence to make things simpler, the assumption that the trade timestamp has 1:1 mapping with the quote timestamp was made here.
3. MAX_CACHE_SIZE for the caches should be configured optimally according to the throughput of the quote feed so that the candidate entries are stale enough that its very unlikely that a trade would arrive on this quote anymore.
4. Serializing and publishing the output message out in the OutputService has not been implemented partially due to underlying platform / infra dependendence.