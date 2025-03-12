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

    var data = {"OkPercent": 99.97266763848397, "KoPercent": 0.027332361516034985};
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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.6269588192419825, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.9859578736208626, 500, 1500, "23 Get Stock Portfolio Request"], "isController": false}, {"data": [0.2705, 500, 1500, "16 Get Stock Prices Request"], "isController": false}, {"data": [0.062, 500, 1500, "14 Register Request"], "isController": false}, {"data": [1.0, 500, 1500, "Debug Sampler"], "isController": false}, {"data": [0.7642928786359077, 500, 1500, "19 Place Stock Order Request"], "isController": false}, {"data": [0.8149448345035105, 500, 1500, "20 Get Stock Transactions Request"], "isController": false}, {"data": [0.4262788365095286, 500, 1500, "17 Add Money Request"], "isController": false}, {"data": [0.6504513540621866, 500, 1500, "18 Get Wallet Balance Request"], "isController": false}, {"data": [0.9764292878635907, 500, 1500, "22 Get Wallet Balance Request"], "isController": false}, {"data": [0.014, 500, 1500, "15 Login Request"], "isController": false}, {"data": [0.936308926780341, 500, 1500, "21 Get Wallet Transactions Request"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 10976, 3, 0.027332361516034985, 1781.1577077259585, 0, 17267, 350.0, 6143.300000000001, 8155.149999999992, 11195.379999999997, 352.28038643001577, 182.386908439516, 108.45961957786372], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["23 Get Stock Portfolio Request", 997, 0, 0.0, 61.192577733199606, 1, 1333, 7.0, 200.60000000000014, 377.1999999999998, 675.1599999999999, 131.87830687830686, 54.99222366898148, 45.028702670304234], "isController": false}, {"data": ["16 Get Stock Prices Request", 1000, 3, 0.3, 3158.2619999999984, 2, 14297, 2419.5, 7171.5, 8406.499999999995, 11528.830000000005, 39.687264356867885, 18.790330632118902, 13.434487798646664], "isController": false}, {"data": ["14 Register Request", 1000, 0, 0.0, 5379.289000000012, 364, 17267, 4900.0, 10311.4, 11356.849999999993, 13434.75, 41.909391894723605, 15.18396913373287, 11.5304442264574], "isController": false}, {"data": ["Debug Sampler", 997, 0, 0.0, 0.3049147442326985, 0, 23, 0.0, 1.0, 1.0, 2.0, 135.72011979308468, 188.56855559147834, 0.0], "isController": false}, {"data": ["19 Place Stock Order Request", 997, 0, 0.0, 737.6359077231685, 9, 9522, 347.0, 1691.8000000000004, 3126.5999999999995, 6797.379999999994, 49.77036741214057, 18.032037412015775, 21.708235573083066], "isController": false}, {"data": ["20 Get Stock Transactions Request", 997, 0, 0.0, 485.41323971915773, 3, 5911, 326.0, 1051.2, 1582.6999999999975, 3639.1199999999944, 92.36612933110987, 53.2692496989068, 31.80821665971836], "isController": false}, {"data": ["17 Add Money Request", 997, 0, 0.0, 2262.9789368104307, 8, 14230, 1260.0, 6402.4, 7688.499999999996, 9256.459999999997, 40.32029765034173, 14.608232840114045, 15.184525960994055], "isController": false}, {"data": ["18 Get Wallet Balance Request", 997, 0, 0.0, 1174.5336008024092, 2, 10640, 465.0, 3588.0, 4994.199999999998, 8650.499999999998, 43.10046688569946, 16.162675082137298, 14.674189839832266], "isController": false}, {"data": ["22 Get Wallet Balance Request", 997, 0, 0.0, 102.52557673019041, 1, 1542, 12.0, 338.0, 489.0999999999999, 890.1199999999999, 118.47890671420083, 44.31388796048723, 40.33789178550208], "isController": false}, {"data": ["15 Login Request", 1000, 0, 0.0, 6015.7520000000095, 473, 14786, 5637.0, 9820.9, 11130.649999999992, 12893.67, 40.29171199484266, 21.35118413564205, 10.006824408719126], "isController": false}, {"data": ["21 Get Wallet Transactions Request", 997, 0, 0.0, 187.13440320962886, 1, 4238, 45.0, 563.2000000000005, 682.3999999999996, 1444.8799999999992, 102.41397021058037, 48.462578646635855, 35.368415350539294], "isController": false}]}, function(index, item){
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
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": [{"data": ["Assertion failed", 3, 100.0, 0.027332361516034985], "isController": false}]}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 10976, 3, "Assertion failed", 3, "", "", "", "", "", "", "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": ["16 Get Stock Prices Request", 1000, 3, "Assertion failed", 3, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
