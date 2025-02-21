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

    var data = {"OkPercent": 34.48275862068966, "KoPercent": 65.51724137931035};
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
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.3448275862068966, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.0, 500, 1500, "27 Invalid Payload Example"], "isController": false}, {"data": [1.0, 500, 1500, "4 Login Request"], "isController": false}, {"data": [0.0, 500, 1500, "11 Place Stock Order Request"], "isController": false}, {"data": [0.0, 500, 1500, "19 Place Stock Order Request"], "isController": false}, {"data": [1.0, 500, 1500, "8 Add Apple Stock Request"], "isController": false}, {"data": [0.0, 500, 1500, "9 Get Stock Portfolio Request"], "isController": false}, {"data": [0.0, 500, 1500, "13 Get Stock Transactions Request"], "isController": false}, {"data": [0.0, 500, 1500, "23 Get Stock Portfolio Request"], "isController": false}, {"data": [0.0, 500, 1500, "16 Get Stock Prices Request"], "isController": false}, {"data": [0.0, 500, 1500, "3 Failed Login Request"], "isController": false}, {"data": [0.0, 500, 1500, "10 Place Stock Order Request"], "isController": false}, {"data": [1.0, 500, 1500, "17 Add Money Request"], "isController": false}, {"data": [0.0, 500, 1500, "1 Register Request"], "isController": false}, {"data": [0.0, 500, 1500, "14 Register Request"], "isController": false}, {"data": [1.0, 500, 1500, "6 Add Google Stock Request"], "isController": false}, {"data": [0.0, 500, 1500, "2 Failed Register Request"], "isController": false}, {"data": [0.0, 500, 1500, "22 Get Wallet Balance Request"], "isController": false}, {"data": [1.0, 500, 1500, "7 Create Apple Stock Request"], "isController": false}, {"data": [1.0, 500, 1500, "15 Login Request"], "isController": false}, {"data": [0.0, 500, 1500, "25 Get Stock Transactions Request"], "isController": false}, {"data": [0.0, 500, 1500, "21 Get Wallet Transactions Request"], "isController": false}, {"data": [0.0, 500, 1500, "12 Get Stock Portfolio Request"], "isController": false}, {"data": [1.0, 500, 1500, "26 Invalid Token"], "isController": false}, {"data": [1.0, 500, 1500, "Debug Sampler"], "isController": false}, {"data": [0.0, 500, 1500, "20 Get Stock Transactions Request"], "isController": false}, {"data": [0.0, 500, 1500, "18 Get Wallet Balance Request"], "isController": false}, {"data": [1.0, 500, 1500, "5 Create Google Stock Request"], "isController": false}, {"data": [0.0, 500, 1500, "24 Cancel Stock Order Request"], "isController": false}]}, function(index, item){
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
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 29, 19, 65.51724137931035, 34.0, 2, 132, 16.0, 99.0, 115.5, 132.0, 2.0525161016349354, 0.9922521940689363, 0.6372642260598769], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["27 Invalid Payload Example", 1, 1, 100.0, 22.0, 22, 22, 22.0, 22.0, 22.0, 22.0, 45.45454545454545, 16.468394886363637, 17.001065340909093], "isController": false}, {"data": ["4 Login Request", 1, 0, 0.0, 99.0, 99, 99, 99.0, 99.0, 99.0, 99.0, 10.101010101010102, 5.316840277777778, 2.3575599747474745], "isController": false}, {"data": ["11 Place Stock Order Request", 1, 1, 100.0, 14.0, 14, 14, 14.0, 14.0, 14.0, 14.0, 71.42857142857143, 26.85546875, 31.040736607142858], "isController": false}, {"data": ["19 Place Stock Order Request", 1, 1, 100.0, 11.0, 11, 11, 11.0, 11.0, 11.0, 11.0, 90.9090909090909, 34.1796875, 38.0859375], "isController": false}, {"data": ["8 Add Apple Stock Request", 1, 0, 0.0, 17.0, 17, 17, 17.0, 17.0, 17.0, 17.0, 58.8235294117647, 21.312040441176467, 22.518382352941174], "isController": false}, {"data": ["9 Get Stock Portfolio Request", 1, 1, 100.0, 16.0, 16, 16, 16.0, 16.0, 16.0, 16.0, 62.5, 58.89892578125, 21.1181640625], "isController": false}, {"data": ["13 Get Stock Transactions Request", 1, 1, 100.0, 17.0, 17, 17, 17.0, 17.0, 17.0, 17.0, 58.8235294117647, 21.197150735294116, 20.048253676470587], "isController": false}, {"data": ["23 Get Stock Portfolio Request", 1, 1, 100.0, 12.0, 12, 12, 12.0, 12.0, 12.0, 12.0, 83.33333333333333, 30.029296875, 28.157552083333332], "isController": false}, {"data": ["16 Get Stock Prices Request", 1, 1, 100.0, 10.0, 10, 10, 10.0, 10.0, 10.0, 10.0, 100.0, 36.03515625, 33.49609375], "isController": false}, {"data": ["3 Failed Login Request", 1, 1, 100.0, 95.0, 95, 95, 95.0, 95.0, 95.0, 95.0, 10.526315789473683, 3.895970394736842, 2.4671052631578947], "isController": false}, {"data": ["10 Place Stock Order Request", 1, 1, 100.0, 12.0, 12, 12, 12.0, 12.0, 12.0, 12.0, 83.33333333333333, 31.331380208333332, 36.1328125], "isController": false}, {"data": ["17 Add Money Request", 1, 0, 0.0, 16.0, 16, 16, 16.0, 16.0, 16.0, 16.0, 62.5, 22.64404296875, 23.3154296875], "isController": false}, {"data": ["1 Register Request", 1, 1, 100.0, 132.0, 132, 132, 132.0, 132.0, 132.0, 132.0, 7.575757575757576, 2.803918087121212, 1.975319602272727], "isController": false}, {"data": ["14 Register Request", 1, 1, 100.0, 96.0, 96, 96, 96.0, 96.0, 96.0, 96.0, 10.416666666666666, 3.8553873697916665, 2.7567545572916665], "isController": false}, {"data": ["6 Add Google Stock Request", 1, 0, 0.0, 24.0, 24, 24, 24.0, 24.0, 24.0, 24.0, 41.666666666666664, 15.096028645833332, 15.909830729166666], "isController": false}, {"data": ["2 Failed Register Request", 1, 1, 100.0, 99.0, 99, 99, 99.0, 99.0, 99.0, 99.0, 10.101010101010102, 3.7385574494949494, 2.623895202020202], "isController": false}, {"data": ["22 Get Wallet Balance Request", 1, 1, 100.0, 13.0, 13, 13, 13.0, 13.0, 13.0, 13.0, 76.92307692307693, 29.071514423076923, 25.916466346153847], "isController": false}, {"data": ["7 Create Apple Stock Request", 1, 0, 0.0, 18.0, 18, 18, 18.0, 18.0, 18.0, 18.0, 55.55555555555555, 20.833333333333336, 20.45355902777778], "isController": false}, {"data": ["15 Login Request", 1, 0, 0.0, 96.0, 96, 96, 96.0, 96.0, 96.0, 96.0, 10.416666666666666, 5.482991536458333, 2.4515787760416665], "isController": false}, {"data": ["25 Get Stock Transactions Request", 1, 1, 100.0, 17.0, 17, 17, 17.0, 17.0, 17.0, 17.0, 58.8235294117647, 21.197150735294116, 20.048253676470587], "isController": false}, {"data": ["21 Get Wallet Transactions Request", 1, 1, 100.0, 16.0, 16, 16, 16.0, 16.0, 16.0, 16.0, 62.5, 22.52197265625, 21.3623046875], "isController": false}, {"data": ["12 Get Stock Portfolio Request", 1, 1, 100.0, 15.0, 15, 15, 15.0, 15.0, 15.0, 15.0, 66.66666666666667, 62.825520833333336, 22.526041666666668], "isController": false}, {"data": ["26 Invalid Token", 1, 0, 0.0, 7.0, 7, 7, 7.0, 7.0, 7.0, 7.0, 142.85714285714286, 56.36160714285714, 30.970982142857142], "isController": false}, {"data": ["Debug Sampler", 2, 0, 0.0, 2.5, 2, 3, 2.5, 3.0, 3.0, 3.0, 0.3749531308586427, 0.4871827544994376, 0.0], "isController": false}, {"data": ["20 Get Stock Transactions Request", 1, 1, 100.0, 18.0, 18, 18, 18.0, 18.0, 18.0, 18.0, 55.55555555555555, 20.01953125, 18.934461805555557], "isController": false}, {"data": ["18 Get Wallet Balance Request", 1, 1, 100.0, 12.0, 12, 12, 12.0, 12.0, 12.0, 12.0, 83.33333333333333, 31.494140625, 28.076171875], "isController": false}, {"data": ["5 Create Google Stock Request", 1, 0, 0.0, 66.0, 66, 66, 66.0, 66.0, 66.0, 66.0, 15.151515151515152, 5.66702178030303, 5.5930397727272725], "isController": false}, {"data": ["24 Cancel Stock Order Request", 1, 1, 100.0, 11.0, 11, 11, 11.0, 11.0, 11.0, 11.0, 90.9090909090909, 33.11434659090909, 34.1796875], "isController": false}]}, function(index, item){
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
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": [{"data": ["400", 4, 21.05263157894737, 13.793103448275861], "isController": false}, {"data": ["500", 4, 21.05263157894737, 13.793103448275861], "isController": false}, {"data": ["Value in json path '$.success' expected to match regexp 'false', but it did not match: 'true'", 1, 5.2631578947368425, 3.4482758620689653], "isController": false}, {"data": ["Assertion failed", 10, 52.63157894736842, 34.48275862068966], "isController": false}]}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 29, 19, "Assertion failed", 10, "400", 4, "500", 4, "Value in json path '$.success' expected to match regexp 'false', but it did not match: 'true'", 1, "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": ["27 Invalid Payload Example", 1, 1, "Value in json path '$.success' expected to match regexp 'false', but it did not match: 'true'", 1, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": ["11 Place Stock Order Request", 1, 1, "400", 1, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["19 Place Stock Order Request", 1, 1, "400", 1, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": ["9 Get Stock Portfolio Request", 1, 1, "Assertion failed", 1, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["13 Get Stock Transactions Request", 1, 1, "Assertion failed", 1, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["23 Get Stock Portfolio Request", 1, 1, "Assertion failed", 1, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["16 Get Stock Prices Request", 1, 1, "Assertion failed", 1, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["3 Failed Login Request", 1, 1, "500", 1, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["10 Place Stock Order Request", 1, 1, "400", 1, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": ["1 Register Request", 1, 1, "500", 1, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["14 Register Request", 1, 1, "500", 1, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": ["2 Failed Register Request", 1, 1, "500", 1, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["22 Get Wallet Balance Request", 1, 1, "Assertion failed", 1, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": ["25 Get Stock Transactions Request", 1, 1, "Assertion failed", 1, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["21 Get Wallet Transactions Request", 1, 1, "Assertion failed", 1, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["12 Get Stock Portfolio Request", 1, 1, "Assertion failed", 1, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": ["20 Get Stock Transactions Request", 1, 1, "Assertion failed", 1, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["18 Get Wallet Balance Request", 1, 1, "Assertion failed", 1, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": ["24 Cancel Stock Order Request", 1, 1, "400", 1, "", "", "", "", "", "", "", ""], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
