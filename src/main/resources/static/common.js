window.onload = function () {
  if (sessionStorage.getItem("dbUrl")) {
    document.querySelector("#db-url").value = sessionStorage.getItem("dbUrl");
    document.querySelector("#db-port").value = sessionStorage.getItem("dbPort");
    document.querySelector("#db-database").value =
      sessionStorage.getItem("dbDatabase");
    document.querySelector("#db-user").value =
      sessionStorage.getItem("dbUsername");
    document.querySelector("#db-password").value =
      sessionStorage.getItem("dbPassword");

    document.querySelector("#workspace").value = sessionStorage.getItem(
      "workspace",
      workspace
    );
    document.querySelector("#package").value = sessionStorage.getItem(
      "package",
      package
    );
  }

  const connectBtn = document.querySelector("#db-connect-btn");
  connectBtn.addEventListener("click", async function () {
    const dbUrl = document.querySelector("#db-url").value;
    const dbPort = document.querySelector("#db-port").value;
    const dbDatabase = document.querySelector("#db-database").value;
    const dbUsername = document.querySelector("#db-user").value;
    const dbPassword = document.querySelector("#db-password").value;

    if (dbUrl.trim() === "") {
      alert("URL을 입력하세요.");
      document.querySelector("#db-url").focus();
    } else if (dbPort.trim() === "") {
      alert("Port를 입력하세요.");
      document.querySelector("#db-port").focus();
    } else if (dbDatabase.trim() === "") {
      alert("데이터베이스 이름을 입력하세요.");
      document.querySelector("#db-database").focus();
    } else if (dbUsername.trim() === "") {
      alert("계정을 입력하세요.");
      document.querySelector("#db-user").focus();
    } else if (dbPassword.trim() === "") {
      alert("비밀번호를 입력하세요.");
      document.querySelector("#db-password").focus();
    } else {
      const connectResponse = await fetch("/connect", {
        method: "post",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          dbUrl,
          dbPort,
          dbDatabase,
          dbUsername,
          dbPassword,
        }),
      });

      const tableList = await connectResponse.json();
      if (tableList && confirm("접속 정보를 저장할까요?")) {
        sessionStorage.setItem("dbUrl", dbUrl);
        sessionStorage.setItem("dbPort", dbPort);
        sessionStorage.setItem("dbDatabase", dbDatabase);
        sessionStorage.setItem("dbUsername", dbUsername);
        sessionStorage.setItem("dbPassword", dbPassword);
      }

      var tableListDom = document.querySelector(".table-list");
      var columnListDom = document.querySelector(".column-list");
      tableListDom.innerHTML = "";
      columnListDom.innerHTML = "";
      if (tableList) {
        const tables = tableList.tables;
        for (const table of tables) {
          var tableItem = document.createElement("li");
          tableItem.innerText = `${table.tableName} (${table.comments})`;
          tableItem.dataset.tableName = table.tableName;
          tableListDom.appendChild(tableItem);

          tableItem.addEventListener("click", async function () {
            document.querySelector("#tableName").value = this.dataset.tableName;

            for (const tableItemDom of document.querySelectorAll(
              ".table-list > li"
            )) {
              tableItemDom.className = "";
            }

            this.className = "active";

            columnListDom.innerHTML = "";
            const connectResponse = await fetch("/columns", {
              method: "post",
              headers: {
                "Content-Type": "application/json",
              },
              body: JSON.stringify({
                dbUrl,
                dbPort,
                dbDatabase,
                dbUsername,
                dbPassword,
                tableName: this.dataset.tableName,
              }),
            });

            const columnList = await connectResponse.json();
            if (columnList) {
              const columns = columnList.columns;
              for (const column of columns) {
                var columnItem = document.createElement("li");
                columnItem.innerText = `${column.columnName} ${column.dataType}${column.length} ${column.comments}`;
                columnListDom.appendChild(columnItem);
              }
            }
          });
        }
      }
    }
  });

  document
    .querySelector("#make-vo-btn")
    .addEventListener("click", async function () {
      const dbUrl = document.querySelector("#db-url").value;
      const dbPort = document.querySelector("#db-port").value;
      const dbDatabase = document.querySelector("#db-database").value;
      const dbUsername = document.querySelector("#db-user").value;
      const dbPassword = document.querySelector("#db-password").value;

      const workspace = document.querySelector("#workspace").value;
      const package = document.querySelector("#package").value;
      const tableName = document.querySelector("#tableName").value;
      const makePackages = (
        document.querySelector("#makePackages:checked") || { value: "N" }
      ).value;

      if (workspace.trim() === "") {
        alert("프로젝트 경로를 입력하세요.");
        document.querySelector("#workspace").focus();
      } else if (package.trim() === "") {
        alert("파일을 생성할 패키지를 입력하세요.");
        document.querySelector("#package").focus();
      } else if (tableName.trim() === "") {
        alert("VO로 생성하려는 테이블을 선택하세요.");
      } else if (
        confirm(
          "VO 파일을 생성하면 기존 파일 내용을 잃어버릴 수 있습니다. 계속하시겠습니까?"
        )
      ) {
        sessionStorage.setItem("workspace", workspace);
        sessionStorage.setItem("package", package);

        const makeResponse = await fetch("/make", {
          method: "post",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            dbUrl,
            dbPort,
            dbDatabase,
            dbUsername,
            dbPassword,
            workspace,
            package,
            tableName,
            makePackages,
          }),
        });

        const makeResult = await makeResponse.json();
        alert(makeResult.result);
      }
    });
};
