package org.gz.viztracer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.*;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
class Meta {
    private final String version;

    Meta(String ver) {
        version = ver;
    }
}

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
class TraceEventMetaJson {
    private final long pid;
    private final long tid;
    private final String name;
    private final Map<String, String> args;
    /*
    *    {
		"ph": "M",
		"pid": 32511,
		"tid": 32511,
		"name": "process_name",
		"args": {
			"name": "MainProcess"
		}
	}*/
    String ph = "M";

    TraceEventMetaJson(TraceEvent e, boolean isProcess) {
        pid = e.processId;
        tid = e.threadId;
        if (isProcess) {
            name = "process_name";
            args = new HashMap<>();
            args.put("name", "MainProcess");
        } else {
            name = "thread_name";
            args = new HashMap<>();
            args.put("name", e.threadName);
        }
    }
}

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
class TraceEventJSON {
    /*
    {
    	"pid": 15845,
		"tid": 15845,
		"ts": 14606479869.789,
		"dur": 1576.5,
		"name": "heap_sort (example/src/different_sorts.py:93)",
		"ph": "X",
		"cat": "FEE"
		}
    * */
    private final long pid;
    private final long tid;
    private final double ts;
    private final double dur;
    private final String name;
    String ph = "X";
    String cat = "FEE";

    TraceEventJSON(TraceEvent e) {
        pid = e.processId;
        tid = e.threadId;
        ts = e.timestamp;
        dur = e.duration;
        name = e.method;
    }
}

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
class TraceJSON {
    /*
    {"traceEvents": [],
	"viztracer_metadata": {
		"version": "0.12.3"
	},
	"displayTimeUnit": "ms",
	"file_info": {
		"files": {},
		"functions": {}
	}
    }
    * */
    private final List<Object> traceEvents;
    private final Map<String, Map<String, String>> file_info;
    public Meta viztracer_metadata = new Meta("0.12.3");
    public String displayTimeUnit = "ms";

    TraceJSON(List<TraceEvent> events) {
        // TODO test if unsorted data can cause difference.
        events.sort(Comparator.comparingLong(TraceEvent::sortByTimeStamp));


        Set<Long> pids = new HashSet<>();
        Set<Long> tids = new HashSet<>();
        traceEvents = new ArrayList<>();
        for (TraceEvent e : events) {
            if (!pids.contains(e.processId)) {
                pids.add(e.processId);
                traceEvents.add(new TraceEventMetaJson(e, true));
            }
            if (!tids.contains(e.threadId)) {
                tids.add(e.threadId);
                traceEvents.add(new TraceEventMetaJson(e, false));
            }
        }
        for (TraceEvent e : events) {
            traceEvents.add(new TraceEventJSON(e));
        }
        file_info = new HashMap<>();
        file_info.put("files", new HashMap<>());
        file_info.put("functions", new HashMap<>());
    }
}
