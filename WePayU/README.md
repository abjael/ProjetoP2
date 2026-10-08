# WePayU

Sistema de folha de pagamento desenvolvido em Java. A fachada da aplicação
fica em `br.ufal.ic.p2.wepayu.Facade`; modelos, serviços, persistência,
repositórios, utilitários e exceções estão organizados em pacotes separados.

## Requisitos

- JDK 17 ou superior
- EasyAccept em `WePayU/lib/easyaccept.jar`

## Compilar e executar

No PowerShell, a partir da pasta `WePayU`:

```powershell
$sources = Get-ChildItem src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -cp lib/easyaccept.jar -d out $sources
java -cp "out;lib\easyaccept.jar" Main
```

`Main` seleciona o script de aceitação executado naquela execução. Os scripts
estão em `WePayU/tests`; os arquivos esperados para comparação ficam em
`WePayU/ok`.

## Testar um script diretamente

Com os fontes compilados, execute, por exemplo:

```powershell
java -cp "out;lib\easyaccept.jar" easyaccept.EasyAccept br.ufal.ic.p2.wepayu.Facade tests/us1.txt
```

Os dados persistidos são gravados em `wepayu-dados.xml`. A saída das folhas,
os arquivos compilados, as configurações locais do IntelliJ e os arquivos de
projeto da IDE não fazem parte do código-fonte.
