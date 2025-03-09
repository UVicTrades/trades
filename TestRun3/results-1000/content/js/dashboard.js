/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
var showControllersOnly = false;
var seriesFilter = "";
var filtersOnlySampleSeries = true;

/*
 * Add header in statistics table to group metrics by category
 * format
 *
 */
function summaryTableHeader(header) {
    var newRow = header.insertRow(-1);
    newRow.className = "tablesorter-no-sort";
    var cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Requests";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 3;
    cell.innerHTML = "Executions";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 7;
    cell.innerHTML = "Response Times (ms)";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Throughput";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 2;
    cell.innerHTML = "Network (KB/sec)";
    newRow.appendChild(cell);
}

/*
 * Populates the table identified by id parameter with the specified data and
 * format
 *
 */
function createTable(table, info, formatter, defaultSorts, seriesIndex, headerCreator) {
    var tableRef = table[0];

    // Create header and populate it with data.titles array
    var header = tableRef.createTHead();

    // Call callback is available
    if(headerCreator) {
        headerCreator(header);
    }

    var newRow = header.insertRow(-1);
    for (var index = 0; index < info.titles.length; index++) {
        var cell = document.createElement('th');
        cell.innerHTML = info.titles[index];
        newRow.appendChild(cell);
    }

    var tBody;

    // Create overall body if defined
    if(info.overall){
        tBody = document.createElement('tbody');
        tBody.className = "tablesorter-no-sort";
        tableRef.appendChild(tBody);
        var newRow = tBody.insertRow(-1);
        var data = info.overall.data;
        for(var index=0;index < data.length; index++){
            var cell = newRow.insertCell(-1);
            cell.innerHTML = formatter ? formatter(index, data[index]): data[index];
        }
    }

    // Create regular body
    tBody = document.createElement('tbody');
    tableRef.appendChild(tBody);

    var regexp;
    if(seriesFilter) {
        regexp = new RegExp(seriesFilter, 'i');
    }
    // Populate body with data.items array
    for(var index=0; index < info.items.length; index++){
        var item = info.items[index];
        if((!regexp || filtersOnlySampleSeries && !info.supportsControllersDiscrimination || regexp.test(item.data[seriesIndex]))
                &&
                (!showControllersOnly || !info.supportsControllersDiscrimination || item.isController)){
            if(item.data.length > 0) {
                var newRow = tBody.insertRow(-1);
                for(var col=0; col < item.data.length; col++){
                    var cell = newRow.insertCell(-1);
                    cell.innerHTML = formatter ? formatter(col, item.data[col]) : item.data[col];
                }
            }
        }
    }

    // Add support of columns sort
    table.tablesorter({sortList : defaultSorts});
}

$(document).ready(function() {

    // Customize table sorter default options
    $.extend( $.tablesorter.defaults, {
        theme: 'blue',
        cssInfoBlock: "tablesorter-no-sort",
        widthFixed: true,
        widgets: ['zebra']
    });

    var data = {"OkPercent": 28.62, "KoPercent": 71.38};
    var dataset = [
        {
            "label" : "FAIL",
            "data" : data.KoPercent,
            "color" : "#FF6347"
        },
        {
            "label" : "PASS",
            "data" : data.OkPercent,
            "color" : "#9ACD32"
        }];
    $.plot($("#flot-requests-summary"), dataset, {
        series : {
            pie : {
                show : true,
                radius : 1,
                label : {
                    show : true,
                    radius : 3 / 4,
                    formatter : function(label, series) {
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
                            + label
                            + '<br/>'
                            + Math.round10(series.percent, -2)
                            + '%</div>';
                    },
                    background : {
                        opacity : 0.5,
                        color : '#000'
                    }
                }
            }
        },
        legend : {
            show : true
        }
    });

    // Creates APDEX table
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.25015, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.279, 500, 1500, "23 Get Stock Portfolio Request"], "isController": false}, {"data": [0.342, 500, 1500, "16 Get Stock Prices Request"], "isController": false}, {"data": [0.1205, 500, 1500, "14 Register Request"], "isController": false}, {"data": [0.268, 500, 1500, "19 Place Stock Order Request"], "isController": false}, {"data": [0.2775, 500, 1500, "20 Get Stock Transactions Request"], "isController": false}, {"data": [0.27, 500, 1500, "17 Add Money Request"], "isController": false}, {"data": [0.275, 500, 1500, "18 Get Wallet Balance Request"], "isController": false}, {"data": [0.279, 500, 1500, "22 Get Wallet Balance Request"], "isController": false}, {"data": [0.1115, 500, 1500, "15 Login Request"], "isController": false}, {"data": [0.279, 500, 1500, "21 Get Wallet Transactions Request"], "isController": false}]}, function(index, item){
        switch(index){
            case 0:
                item = item.toFixed(3);
                break;
            case 1:
            case 2:
                item = formatDuration(item);
                break;
        }
        return item;
    }, [[0, 0]], 3);

    // Create statistics table
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 10000, 7138, 71.38, 222.64880000000022, 3, 3791, 111.0, 479.0, 757.9499999999989, 2092.959999999999, 456.1211457763182, 654.1923323897328, 57.92359935173782], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["23 Get Stock Portfolio Request", 1000, 721, 72.1, 130.96099999999984, 3, 1132, 69.0, 342.0, 446.74999999999966, 766.7400000000002, 68.01333061280012, 77.13734548221451, 10.462031026831259], "isController": false}, {"data": ["16 Get Stock Prices Request", 1000, 649, 64.9, 180.30200000000013, 4, 1114, 116.5, 431.79999999999995, 531.7499999999997, 842.6900000000003, 63.26311127981274, 102.58094959907002, 6.804800187416967], "isController": false}, {"data": ["14 Register Request", 1000, 721, 72.1, 439.9580000000005, 6, 3791, 148.0, 1371.6999999999994, 2196.85, 2898.87, 64.98570314530804, 111.76322459059007, 5.005295319404731], "isController": false}, {"data": ["19 Place Stock Order Request", 1000, 721, 72.1, 181.46100000000007, 7, 1106, 122.5, 429.5999999999999, 542.8999999999999, 790.99, 65.73757559821195, 93.79551556747963, 10.85677887933868], "isController": false}, {"data": ["20 Get Stock Transactions Request", 1000, 721, 72.1, 158.69100000000023, 7, 1062, 100.0, 361.9, 474.94999999999993, 874.8000000000002, 65.98482349059717, 92.65777074397889, 8.792542168426262], "isController": false}, {"data": ["17 Add Money Request", 1000, 721, 72.1, 189.83400000000015, 4, 1166, 128.0, 439.69999999999993, 525.0, 750.96, 64.64124111182936, 92.23124646493213, 8.996810863768584], "isController": false}, {"data": ["18 Get Wallet Balance Request", 1000, 721, 72.1, 166.681, 3, 1131, 107.0, 372.9, 522.8999999999999, 807.95, 64.92663290481755, 97.1092557987599, 7.603136970847943], "isController": false}, {"data": ["22 Get Wallet Balance Request", 1000, 721, 72.1, 137.90300000000025, 3, 1102, 79.5, 350.69999999999993, 445.89999999999986, 798.95, 66.88068485821296, 79.16981476140316, 9.849813674592028], "isController": false}, {"data": ["15 Login Request", 1000, 721, 72.1, 498.55200000000036, 6, 3261, 239.0, 1476.8, 1994.7499999999995, 2716.95, 63.576832602199765, 101.55386994166827, 5.821005388136563], "isController": false}, {"data": ["21 Get Wallet Transactions Request", 1000, 721, 72.1, 142.14499999999987, 3, 1107, 87.0, 342.9, 452.6499999999995, 775.9100000000001, 66.02403274792024, 87.97205893882872, 9.110478323484749], "isController": false}]}, function(index, item){
        switch(index){
            // Errors pct
            case 3:
                item = item.toFixed(2) + '%';
                break;
            // Mean
            case 4:
            // Mean
            case 7:
            // Median
            case 8:
            // Percentile 1
            case 9:
            // Percentile 2
            case 10:
            // Percentile 3
            case 11:
            // Throughput
            case 12:
            // Kbytes/s
            case 13:
            // Sent Kbytes/s
                item = item.toFixed(2);
                break;
        }
        return item;
    }, [[0, 0]], 0, summaryTableHeader);

    // Create error table
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": [{"data": ["400", 1561, 21.86887083216587, 15.61], "isController": false}, {"data": ["Non HTTP response code: java.net.BindException/Non HTTP response message: Cannot assign requested address", 5576, 78.11711964135613, 55.76], "isController": false}, {"data": ["Assertion failed", 1, 0.014009526478005043, 0.01], "isController": false}]}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 10000, 7138, "Non HTTP response code: java.net.BindException/Non HTTP response message: Cannot assign requested address", 5576, "400", 1561, "Assertion failed", 1, "", "", "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": ["23 Get Stock Portfolio Request", 1000, 721, "Non HTTP response code: java.net.BindException/Non HTTP response message: Cannot assign requested address", 399, "400", 322, "", "", "", "", "", ""], "isController": false}, {"data": ["16 Get Stock Prices Request", 1000, 649, "Non HTTP response code: java.net.BindException/Non HTTP response message: Cannot assign requested address", 648, "Assertion failed", 1, "", "", "", "", "", ""], "isController": false}, {"data": ["14 Register Request", 1000, 721, "Non HTTP response code: java.net.BindException/Non HTTP response message: Cannot assign requested address", 721, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["19 Place Stock Order Request", 1000, 721, "Non HTTP response code: java.net.BindException/Non HTTP response message: Cannot assign requested address", 564, "400", 157, "", "", "", "", "", ""], "isController": false}, {"data": ["20 Get Stock Transactions Request", 1000, 721, "Non HTTP response code: java.net.BindException/Non HTTP response message: Cannot assign requested address", 520, "400", 201, "", "", "", "", "", ""], "isController": false}, {"data": ["17 Add Money Request", 1000, 721, "Non HTTP response code: java.net.BindException/Non HTTP response message: Cannot assign requested address", 564, "400", 157, "", "", "", "", "", ""], "isController": false}, {"data": ["18 Get Wallet Balance Request", 1000, 721, "Non HTTP response code: java.net.BindException/Non HTTP response message: Cannot assign requested address", 599, "400", 122, "", "", "", "", "", ""], "isController": false}, {"data": ["22 Get Wallet Balance Request", 1000, 721, "Non HTTP response code: java.net.BindException/Non HTTP response message: Cannot assign requested address", 432, "400", 289, "", "", "", "", "", ""], "isController": false}, {"data": ["15 Login Request", 1000, 721, "Non HTTP response code: java.net.BindException/Non HTTP response message: Cannot assign requested address", 632, "400", 89, "", "", "", "", "", ""], "isController": false}, {"data": ["21 Get Wallet Transactions Request", 1000, 721, "Non HTTP response code: java.net.BindException/Non HTTP response message: Cannot assign requested address", 497, "400", 224, "", "", "", "", "", ""], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
