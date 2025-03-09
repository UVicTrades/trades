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

    var data = {"OkPercent": 99.922, "KoPercent": 0.078};
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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.05553, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.3824, 500, 1500, "23 Get Stock Portfolio Request"], "isController": false}, {"data": [0.002, 500, 1500, "16 Get Stock Prices Request"], "isController": false}, {"data": [0.0033, 500, 1500, "14 Register Request"], "isController": false}, {"data": [1.0E-4, 500, 1500, "19 Place Stock Order Request"], "isController": false}, {"data": [0.003, 500, 1500, "20 Get Stock Transactions Request"], "isController": false}, {"data": [0.0128, 500, 1500, "17 Add Money Request"], "isController": false}, {"data": [0.0018, 500, 1500, "18 Get Wallet Balance Request"], "isController": false}, {"data": [0.1242, 500, 1500, "22 Get Wallet Balance Request"], "isController": false}, {"data": [0.0, 500, 1500, "15 Login Request"], "isController": false}, {"data": [0.0257, 500, 1500, "21 Get Wallet Transactions Request"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 50000, 39, 0.078, 13156.394440000051, 0, 69956, 2507.5, 5221.0, 5556.950000000001, 6169.980000000003, 341.00596760443307, 146.75341671994886, 113.14882379582268], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["23 Get Stock Portfolio Request", 5000, 0, 0.0, 1371.6386000000025, 0, 5043, 1415.0, 2116.0, 2518.8999999999996, 3442.959999999999, 323.79225488926306, 135.01884066183138, 108.77396062686181], "isController": false}, {"data": ["16 Get Stock Prices Request", 5000, 39, 0.78, 27723.6802, 1328, 50424, 27707.0, 47753.5, 48980.95, 49692.979999999996, 40.36978725122119, 19.1023131257317, 13.443454543619554], "isController": false}, {"data": ["14 Register Request", 5000, 0, 0.0, 30899.179999999924, 360, 69956, 29844.0, 50507.9, 56543.99999999988, 63389.94, 67.02143345441871, 24.282179503505223, 17.588112764399558], "isController": false}, {"data": ["19 Place Stock Order Request", 5000, 0, 0.0, 4114.846000000004, 1404, 33799, 4085.0, 5642.0, 5783.95, 6213.9299999999985, 88.46426043878273, 32.05101623319179, 38.098377786624205], "isController": false}, {"data": ["20 Get Stock Transactions Request", 5000, 0, 0.0, 4528.5495999999985, 983, 7145, 4811.0, 5822.600000000002, 6033.95, 6360.969999999999, 198.96538002387584, 115.05495641414645, 67.42283873855949], "isController": false}, {"data": ["17 Add Money Request", 5000, 0, 0.0, 7266.787600000001, 1315, 49891, 3045.0, 25560.90000000033, 45220.299999999996, 47964.84, 40.17936066601308, 14.557170710049663, 14.910309622153292], "isController": false}, {"data": ["18 Get Wallet Balance Request", 5000, 0, 0.0, 3664.7392000000077, 1318, 49326, 3277.0, 4645.500000000003, 5447.749999999999, 24183.81999999993, 48.49660523763336, 18.186226964112514, 16.244468355965083], "isController": false}, {"data": ["22 Get Wallet Balance Request", 5000, 0, 0.0, 2022.4047999999982, 2, 5877, 1868.0, 3104.7000000000016, 4060.0, 4877.959999999999, 246.56048128605948, 92.21939876226638, 82.58812996202968], "isController": false}, {"data": ["15 Login Request", 5000, 0, 0.0, 46764.06419999997, 2341, 57170, 50122.0, 51931.4, 52644.95, 54840.26999999998, 42.48413217663203, 22.27927634653457, 10.040195299555617], "isController": false}, {"data": ["21 Get Wallet Transactions Request", 5000, 0, 0.0, 3208.0542000000023, 249, 6409, 3081.5, 4653.900000000001, 5263.0, 5754.98, 215.20185934406476, 102.16720881843419, 73.1350068864595], "isController": false}]}, function(index, item){
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
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": [{"data": ["Assertion failed", 39, 100.0, 0.078], "isController": false}]}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 50000, 39, "Assertion failed", 39, "", "", "", "", "", "", "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": ["16 Get Stock Prices Request", 5000, 39, "Assertion failed", 39, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
