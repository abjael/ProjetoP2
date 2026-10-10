# ProjetoP2

Sistema de folha de pagamento desenvolvido em Java para a disciplina de
Programacao 2. A fachada da aplicacao fica em
`br.ufal.ic.p2.wepayu.Facade`; modelos, servicos, persistencia, repositorios,
utilitarios e excecoes estao organizados em pacotes separados.

## Requisitos

- JDK 17 ou superior
- EasyAccept em `WePayU/WePayU/lib/easyaccept.jar`

## Compilar e executar

No PowerShell, a partir da raiz do repositorio:

```powershell
Set-Location .\WePayU\WePayU
$sources = Get-ChildItem src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -cp lib/easyaccept.jar -d out $sources
java -cp "out;lib\easyaccept.jar" Main
```

`Main` seleciona o script de aceitacao executado naquela execucao. Os scripts
estao em `tests`; os arquivos esperados para comparacao ficam em `ok`.

## Executar um script diretamente

Com os fontes compilados e estando na pasta `WePayU\WePayU`:

```powershell
java -cp "out;lib\easyaccept.jar" easyaccept.EasyAccept br.ufal.ic.p2.wepayu.Facade tests/us1.txt
```

Os dados persistidos ficam em `wepayu-dados.xml`. Os arquivos compilados,
configuracoes locais do IntelliJ, arquivos de projeto da IDE e saidas de folha
geradas nao fazem parte do codigo-fonte.
