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

    var data = {"OkPercent": 58.62068965517241, "KoPercent": 41.37931034482759};
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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.5862068965517241, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.0, 500, 1500, "27 Invalid Payload Example"], "isController": false}, {"data": [1.0, 500, 1500, "4 Login Request"], "isController": false}, {"data": [1.0, 500, 1500, "11 Place Stock Order Request"], "isController": false}, {"data": [1.0, 500, 1500, "19 Place Stock Order Request"], "isController": false}, {"data": [1.0, 500, 1500, "8 Add Apple Stock Request"], "isController": false}, {"data": [1.0, 500, 1500, "9 Get Stock Portfolio Request"], "isController": false}, {"data": [0.0, 500, 1500, "13 Get Stock Transactions Request"], "isController": false}, {"data": [0.0, 500, 1500, "23 Get Stock Portfolio Request"], "isController": false}, {"data": [0.0, 500, 1500, "16 Get Stock Prices Request"], "isController": false}, {"data": [0.0, 500, 1500, "3 Failed Login Request"], "isController": false}, {"data": [1.0, 500, 1500, "10 Place Stock Order Request"], "isController": false}, {"data": [1.0, 500, 1500, "17 Add Money Request"], "isController": false}, {"data": [0.0, 500, 1500, "1 Register Request"], "isController": false}, {"data": [0.0, 500, 1500, "14 Register Request"], "isController": false}, {"data": [1.0, 500, 1500, "6 Add Google Stock Request"], "isController": false}, {"data": [0.0, 500, 1500, "2 Failed Register Request"], "isController": false}, {"data": [0.0, 500, 1500, "22 Get Wallet Balance Request"], "isController": false}, {"data": [1.0, 500, 1500, "7 Create Apple Stock Request"], "isController": false}, {"data": [1.0, 500, 1500, "15 Login Request"], "isController": false}, {"data": [1.0, 500, 1500, "25 Get Stock Transactions Request"], "isController": false}, {"data": [0.0, 500, 1500, "21 Get Wallet Transactions Request"], "isController": false}, {"data": [1.0, 500, 1500, "12 Get Stock Portfolio Request"], "isController": false}, {"data": [1.0, 500, 1500, "26 Invalid Token"], "isController": false}, {"data": [1.0, 500, 1500, "Debug Sampler"], "isController": false}, {"data": [0.0, 500, 1500, "20 Get Stock Transactions Request"], "isController": false}, {"data": [0.0, 500, 1500, "18 Get Wallet Balance Request"], "isController": false}, {"data": [1.0, 500, 1500, "5 Create Google Stock Request"], "isController": false}, {"data": [1.0, 500, 1500, "24 Cancel Stock Order Request"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 29, 12, 41.37931034482759, 41.03448275862069, 2, 155, 22.0, 99.0, 128.5, 155.0, 2.024008933556672, 1.127531646950028, 0.6283451764028476], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["27 Invalid Payload Example", 1, 1, 100.0, 16.0, 16, 16, 16.0, 16.0, 16.0, 16.0, 62.5, 22.64404296875, 23.37646484375], "isController": false}, {"data": ["4 Login Request", 1, 0, 0.0, 102.0, 102, 102, 102.0, 102.0, 102.0, 102.0, 9.803921568627452, 5.16046262254902, 2.2882199754901964], "isController": false}, {"data": ["11 Place Stock Order Request", 1, 0, 0.0, 29.0, 29, 29, 29.0, 29.0, 29.0, 29.0, 34.48275862068965, 12.493265086206897, 14.951508620689655], "isController": false}, {"data": ["19 Place Stock Order Request", 1, 0, 0.0, 51.0, 51, 51, 51.0, 51.0, 51.0, 51.0, 19.607843137254903, 7.104013480392157, 8.214613970588236], "isController": false}, {"data": ["8 Add Apple Stock Request", 1, 0, 0.0, 32.0, 32, 32, 32.0, 32.0, 32.0, 32.0, 31.25, 11.322021484375, 11.932373046875], "isController": false}, {"data": ["9 Get Stock Portfolio Request", 1, 0, 0.0, 17.0, 17, 17, 17.0, 17.0, 17.0, 17.0, 58.8235294117647, 27.97564338235294, 19.875919117647058], "isController": false}, {"data": ["13 Get Stock Transactions Request", 1, 1, 100.0, 22.0, 22, 22, 22.0, 22.0, 22.0, 22.0, 45.45454545454545, 66.0067471590909, 15.491832386363637], "isController": false}, {"data": ["23 Get Stock Portfolio Request", 1, 1, 100.0, 13.0, 13, 13, 13.0, 13.0, 13.0, 13.0, 76.92307692307693, 36.50841346153846, 25.99158653846154], "isController": false}, {"data": ["16 Get Stock Prices Request", 1, 1, 100.0, 9.0, 9, 9, 9.0, 9.0, 9.0, 9.0, 111.1111111111111, 58.919270833333336, 37.21788194444445], "isController": false}, {"data": ["3 Failed Login Request", 1, 1, 100.0, 99.0, 99, 99, 99.0, 99.0, 99.0, 99.0, 10.101010101010102, 3.7385574494949494, 2.367424242424242], "isController": false}, {"data": ["10 Place Stock Order Request", 1, 0, 0.0, 29.0, 29, 29, 29.0, 29.0, 29.0, 29.0, 34.48275862068965, 12.493265086206897, 14.951508620689655], "isController": false}, {"data": ["17 Add Money Request", 1, 0, 0.0, 21.0, 21, 21, 21.0, 21.0, 21.0, 21.0, 47.61904761904761, 17.252604166666664, 17.764136904761905], "isController": false}, {"data": ["1 Register Request", 1, 1, 100.0, 155.0, 155, 155, 155.0, 155.0, 155.0, 155.0, 6.451612903225806, 2.387852822580645, 1.6822076612903225], "isController": false}, {"data": ["14 Register Request", 1, 1, 100.0, 97.0, 97, 97, 97.0, 97.0, 97.0, 97.0, 10.309278350515465, 3.8156411082474224, 2.7283344072164946], "isController": false}, {"data": ["6 Add Google Stock Request", 1, 0, 0.0, 50.0, 50, 50, 50.0, 50.0, 50.0, 50.0, 20.0, 7.24609375, 7.63671875], "isController": false}, {"data": ["2 Failed Register Request", 1, 1, 100.0, 95.0, 95, 95, 95.0, 95.0, 95.0, 95.0, 10.526315789473683, 3.895970394736842, 2.734375], "isController": false}, {"data": ["22 Get Wallet Balance Request", 1, 1, 100.0, 15.0, 15, 15, 15.0, 15.0, 15.0, 15.0, 66.66666666666667, 25.1953125, 22.4609375], "isController": false}, {"data": ["7 Create Apple Stock Request", 1, 0, 0.0, 93.0, 93, 93, 93.0, 93.0, 93.0, 93.0, 10.752688172043012, 4.021757392473118, 3.958753360215054], "isController": false}, {"data": ["15 Login Request", 1, 0, 0.0, 95.0, 95, 95, 95.0, 95.0, 95.0, 95.0, 10.526315789473683, 5.540707236842105, 2.4773848684210527], "isController": false}, {"data": ["25 Get Stock Transactions Request", 1, 0, 0.0, 24.0, 24, 24, 24.0, 24.0, 24.0, 24.0, 41.666666666666664, 69.74283854166667, 14.200846354166666], "isController": false}, {"data": ["21 Get Wallet Transactions Request", 1, 1, 100.0, 19.0, 19, 19, 19.0, 19.0, 19.0, 19.0, 52.63157894736842, 30.736019736842106, 17.989309210526315], "isController": false}, {"data": ["12 Get Stock Portfolio Request", 1, 0, 0.0, 15.0, 15, 15, 15.0, 15.0, 15.0, 15.0, 66.66666666666667, 24.0234375, 22.526041666666668], "isController": false}, {"data": ["26 Invalid Token", 1, 0, 0.0, 7.0, 7, 7, 7.0, 7.0, 7.0, 7.0, 142.85714285714286, 56.36160714285714, 30.970982142857142], "isController": false}, {"data": ["Debug Sampler", 2, 0, 0.0, 2.0, 2, 2, 2.0, 2.0, 2.0, 2.0, 0.3709886848451122, 0.4604752133184938, 0.0], "isController": false}, {"data": ["20 Get Stock Transactions Request", 1, 1, 100.0, 22.0, 22, 22, 22.0, 22.0, 22.0, 22.0, 45.45454545454545, 35.99964488636364, 15.491832386363637], "isController": false}, {"data": ["18 Get Wallet Balance Request", 1, 1, 100.0, 17.0, 17, 17, 17.0, 17.0, 17.0, 17.0, 58.8235294117647, 22.231158088235293, 19.81847426470588], "isController": false}, {"data": ["5 Create Google Stock Request", 1, 0, 0.0, 21.0, 21, 21, 21.0, 21.0, 21.0, 21.0, 47.61904761904761, 17.81063988095238, 17.578125], "isController": false}, {"data": ["24 Cancel Stock Order Request", 1, 0, 0.0, 21.0, 21, 21, 21.0, 21.0, 21.0, 21.0, 47.61904761904761, 17.252604166666664, 17.950148809523807], "isController": false}]}, function(index, item){
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
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": [{"data": ["500", 4, 33.333333333333336, 13.793103448275861], "isController": false}, {"data": ["Value in json path '$.success' expected to match regexp 'false', but it did not match: 'true'", 1, 8.333333333333334, 3.4482758620689653], "isController": false}, {"data": ["Assertion failed", 7, 58.333333333333336, 24.137931034482758], "isController": false}]}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 29, 12, "Assertion failed", 7, "500", 4, "Value in json path '$.success' expected to match regexp 'false', but it did not match: 'true'", 1, "", "", "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": ["27 Invalid Payload Example", 1, 1, "Value in json path '$.success' expected to match regexp 'false', but it did not match: 'true'", 1, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": ["13 Get Stock Transactions Request", 1, 1, "Assertion failed", 1, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["23 Get Stock Portfolio Request", 1, 1, "Assertion failed", 1, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["16 Get Stock Prices Request", 1, 1, "Assertion failed", 1, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["3 Failed Login Request", 1, 1, "500", 1, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": ["1 Register Request", 1, 1, "500", 1, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["14 Register Request", 1, 1, "500", 1, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": ["2 Failed Register Request", 1, 1, "500", 1, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["22 Get Wallet Balance Request", 1, 1, "Assertion failed", 1, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": ["21 Get Wallet Transactions Request", 1, 1, "Assertion failed", 1, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": ["20 Get Stock Transactions Request", 1, 1, "Assertion failed", 1, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["18 Get Wallet Balance Request", 1, 1, "Assertion failed", 1, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
