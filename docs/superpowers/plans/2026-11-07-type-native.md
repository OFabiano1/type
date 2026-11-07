# TYPE Nativo — Kotlin Compose Desktop — Implementation Plan

> **For agentic workers:** Required SUB-SKILL: superpowers:executing-plans (inline). Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Reconstruir o TYPE como app desktop nativo em Kotlin + Compose Multiplatform (Compose Desktop): editor de texto sem distrações, sidebar com notas (~230px), editor central, barra inferior com palavras/caracteres/estado de salvamento, autosave, pesquisa, Ctrl+N/S/F, temas dark/light, export .md/.txt e `.exe` portátil offline (JDK 17 + Gradle 8.10 + jpackage).

**Spec:** plano do usuário (mensagem de 2026-11-07 no chat) + `axolotl-ai/prompt.txt` (identidade Axolotl BR). Fonte de verdade visual: fundo `#111111`, superfícies `#191919`, texto `#E5E5E5`, fonte JetBrains Mono (fallback: monospace do sistema), placeholder `Enter your text...` quando o documento está vazio.

## Global Constraints

- Kotlin idiomático, arquitetura simples, sem overengineering; UI state reivo Compose.
- Offline: sem rede em runtime; persistência em disco (UTF-8 explícito, gravação atômica — texto não se perde).
- Nunca apagar texto: exclusão de nota move para `Lixeira/` no vault (não `delete`); erro de autosave mantém o texto no editor e sinaliza no status bar.
- Título da nota = primeira linha não-vazia ("Sem título" quando vazio).
- Tema por padrão **dark**; preferência persistida.
- Ícones `type.ico`/`type.png` verbatim (não recriar).
- `.gitignore` de TYPE: `.gradle/`, `build/`, `dist/`.

## Review Focus

1. **Nota vazia/whitespace** — nova folha em branco não polui; título cai em "Sem título".
2. **Roundtrip Unicode/quebras** — acentos, emoji e CRLF sobrevivem a salvar/abrir.
3. **Colisão de nome** — duas novas notas no mesmo segundo: sufixo `-1`, `-2`, nada sobrescrito.
4. **Falha de disco no autosave** — texto permanece no editor; status mostra erro; Ctrl+S retry.
5. **Delete** — remove da lista, arquivo MIGRA para `Lixeira/` (recuperável).

---

### Task 1: Scaffold Compose Desktop + janela principal

**Files:**
- Create: `TYPE/settings.gradle.kts`, `TYPE/gradle.properties`, `TYPE/.gitignore`, `TYPE/gradlew(.bat)+gradle/wrapper/*`, `composeApp/build.gradle.kts`
- Create: `composeApp/src/jvmMain/kotlin/app/type/Main.kt` (janela 1200x760, mínimo 640x400, redimensionável, título TYPE, fundo `#111111`), `composeApp/src/jvmMain/kotlin/app/type/App.kt`, `theme/Colors.kt`
- Keep: `type.ico`, `type.png`, `LICENSE`, `.gitattributes`, `desktop.ini` (não tocar)

**Interfaces:**
- Produces: `.\gradlew.bat :composeApp:run` abre a janela dark; wrapper Gradle 8.10; versões Kotlin 2.1.21 + Compose 1.8.2 (estáveis, compatíveis com JDK 17/Gradle 8.10).

- [ ] **Step 1:** gerar wrapper: `& C:\Users\Fabix\.gradle\wrapper\dists\gradle-8.10-bin\deqhafrv1ntovfmgh0nh3npr9\gradle-8.10\bin\gradle.bat wrapper --gradle-version 8.10`
- [ ] **Step 2:** escrever `settings.gradle.kts` (pluginManagement mavenCentral/gradlePluginPortal; include = `:composeApp`), `gradle.properties`, `composeApp/build.gradle.kts` (kotlin jvm 2.1.21, compose plugin 1.8.2, Compose 1.8.2, desktop currentOs)
- [ ] **Step 3:** `Main.kt` + `App.kt` mínimos com `Window` e cores noCOD
- [ ] **Step 4:** `.\gradlew.bat :composeApp:run` — Expected: janela escura abre, título "TYPE". Fechar. (Smoke manual. Se o `#Projetos` quebrar o tooling: copiar projeto para `C:\Temp\TYPE`, compilar lá, sincronizar fontes — ver AGENTS.md.)
- [ ] **Step 6:** Commit `feat: compose desktop scaffold and main window`

### Task 2: Modelo e repositório de notas (TDD)

**Files:**
- Create: `composeApp/src/jvmTest/kotlin/app/type/data/NoteTest.kt`, `NoteRepositoryTest.kt`
- Create: `data/Note.kt` (Note, notes fns puras: `noteTitle(text)`, `noteSlug(text, max)`, `newNoteFileName(now, existing)`), `data/NoteRepository.kt` (vault `%USERPROFILE%\Documents\TYPE\Notas` — caminho documentos OneDrive-safe via User Shell Folders registry com fallback; list por mtime desc, read, write atômico, create, delete→`Lixeira\`)

**Interfaces:**
- Produces: `NoteRepository`: `list(): List<Note>`, `read(id): String`, `write(id, text): Result<Unit>`, `create(now, text): Note`, `delete(id)`, `openVaultDir()`. Extension -.md e -.txt vetted by `Note.isNoteFile`.

- [ ] **Step 1:** testes falham — arquivo ausente→"", UTF-8 roundtrip (ação 🫟 + CRLF), list por mtime desc, delete move p/ `Lixeira\`, colisão de nome→sufixo `-1`
- [ ] **Step 2:** `.\gradlew.bat :composeApp:jvmTest` — Expected: FAIL
- [ ] **Step 3:** implementar
- [ ] **Step 4:** `.\gradlew.bat :composeApp:jvmTest` — Expected: PASS
- [ ] **Step 5:** Commit `feat: note model and vault repository`

### Task 3: Estatísticas e preferência de tema (TDD)

**Files:**
- Create: `editor/Stats.kt` (`countWords` whitespace não-só-espaços→0, `countChars` = length), `data/AppSettings.kt` (properties em `<vault>\..\TYPE\.type\settings.properties`, chave `theme`)
- Test: `StatsTest.kt`, `AppSettingsTest.kt`

- [ ] **Step 1:** testes RED; **Step 2:** implementar; **Step 3:** PASS; **Step 4:** Commit `feat: text stats and theme preference`

### Task 4: Editor UI + autosave + barra inferior

**Files:**
- Create: `ui/Editor.kt` (BasicTextField largura leitura ~720px central, mono, cursor `#E5E5E5`), `ui/StatusBar.kt` (palavras, caracteres, estado), `editor/EditorState.kt` (texto+dirty, autosave debounce 500ms coroutine, status `idling/saving/saved: HH:MM/saved-error`)
- Modify: `App.kt`

**Interfaces:**
- Consumes: `SheetRepository`, `Stats`, `EditorActions` (salvar, limpar folha, mísseis de tempo pulso…). Produces: composables `EditorSheet(...)`, `StatusBar(...)`.

- [ ] **Step 1:** escrever UI — placeholder `Enter your text...` quando vazio e foco automático
- [ ] **Step 2:** fiação autosave: `launch` em `snapshotFlow`/debounce; WS write no Dispatchers.IO; tentar novamente no próximo input; status via state
- [ ] **Step 3:** smoke manual — digitar → `current.md` no vault tem texto; reabrir restaura; falha de disco (Review Focus 4): renomear pasta TYPE, digitar → texto fica no editor e status "erro ao salvar". Reverter.
- [ ] **Step 4:** Commit `feat: editor with autosave and status bar`

### Task 5: Sidebar, criar/excluir, pesquisa, atalhos

**Files:**
- Create: `ui/Sidebar.kt` (~230px, lista de notas anterior por title, hover p/ rolagem s, ✕ excluir), `ui/NotesModel.kt` (pure `filterNotes(notes, query)` case-insensitive título+conteúdo + estado de lista)
- Modify: `App.kt`, `Registrar.kt`? (título `TYPE — nota atual.hint`)

**Atalhos:** Ctrl+N nova nota, Ctrl+S força salvar, Ctrl+F foco no campo pesquisa — `Modifier.onPreviewKeyEvent` na raiz.

- [ ] **Step 1:** testes RED para `filterNotes` (query vazia→lista toda; match título; match conteúdo; case-insensitive)
- [ ] **Step 2:** implementar sidebar (hóster hover bar, ✕ por linha) + pesquisa; atalhos
- [ ] **Step 3:** PASS; smoke manual: criar, alternar, pesquisar, excluir (→Lixeira), Ctrl+N/S/F
- [ ] **Step 4:** Commit `feat: sidebar, search, create/delete, shortcuts`

### Task 6: Temas dark/light

**Files:**
- Create: `theme/Theme.kt` (TypeTheme dark/light material3 wrappers), `theme/Typography.kt` (JetBrains Mono; se TTF não estiver no resources, usar `FontFamily.Monospace` do sistema — Ruling)
- Modify: `ui/TopBar.kt` (barra superior discreta com toggle tema), `App.kt`

- [ ] **Step 1:** baixar JetBrainsMono-Regular.ttf para `composeApp/src/jvmMain/resources/fonts/` (se falhar offline → fallback, ledger)
- [ ] **Step 2:** smoke manual: toggle alterna, preferência persiste depois de `run`.
- [ ] **Step 3:** Commit `feat: dark and light themes with JetBrains Mono`

### Task 7: Exportar .md/.txt

**Files:**
- Create: `editor/ExportName.kt` (<base>+"."+ext válida, não duplica ext, ext inválida→`.txt`) + test; Modify: `ui/TopBar.kt`/`App.kt` (`\u25BE` menu "Exportar…" com `JFileChooser` filtro .md/.txt, UTF-8)

- [ ] **Step 1:** test RED→GREEN `exportFileName`; **Step 2:** fiação diálogo; **Step 3:** Commit `feat: export current note as md or txt`

### Task 8: Empacotar `.exe` + review final

**Files:**
- Modify: `composeApp/build.gradle.kts` (compose.desktop.application: mainClass `app.type.MainKt`, nativeDistributions `TargetFormat.AppImage` + msi opcional, packageName TYPE,ADOW `windows.iconFile` do type.ico)
- Produces: `composeApp/build/compose/binaries/main/app/TYPE/TYPE.exe` → copiado para `TYPE/dist/TYPE/TYPE.exe`

- [ ] **Step 1:** `.\gradlew.bat createDistributable` / `packageDistributionForCurrentOS` — Expected: EXE gerado; rodar exe (smoke: janela, editor, ícone taskbar se .ico aplicado — se AppImage Windows não embutir .ico, Ruling e seguir).
- [ ] **Step 2:** Commit `build: windows exe with type.ico`
- [ ] **Step 3:** review branch inteira (subagent), fix Critical/Important, finais.
