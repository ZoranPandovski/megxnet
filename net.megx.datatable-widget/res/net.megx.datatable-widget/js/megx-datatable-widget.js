var MegxDatatableWidget = function() {
    var config = {};
    var generateDatatableHtml = function() {
        var self = this;
        var tableDiv = document.getElementById(config.tableDivId);
        var table = document.createElement("table");
        table.id = self.tableId;
        table.setAttribute("class", "display");
        var thead = document.createElement("thead");
        var tr = document.createElement("tr");
        var th;

        for (var i = 0; i < config.columns.length; i++) {

            th = document.createElement("th");
            th.innerText = config.columns[i];
            tr.appendChild(th);

        }

        thead.appendChild(tr);
        var tbody = document.createElement("tbody");
        table.appendChild(thead);
        table.appendChild(tbody);
        tableDiv.appendChild(table);
    };

    var renderDatatable = function() {
        var self = this;
        var tbl = $("#" + self.tableId);
        tbl.addClass("megx_dataTable");
        var cfg = {
            "bJQueryUI": true,
            "bPaginate": true,
            "bFilter": true,
            "bSort": true,
            "sPaginationType": "full_numbers",
            "fnDrawCallback": function(oSettings) {
                this.$('tr').hover(function() {
                    $(this).addClass('highlighted');
                }, function() {
                    $(this).removeClass('highlighted');
                });
            },
            "fnOnServerDataLoad": function() {},
            aoColumns: config.aoColumns
        };
        var oTable = tbl.dataTable(cfg);
        oTable.dataTable().fnAddData(config.data);
    };

    return {

        createTable: function(conf) {
            config = conf || {};
            this.tableId = "t_" + config.tableDivId;
            generateDatatableHtml();
            renderDatatable();
        }
    };


};