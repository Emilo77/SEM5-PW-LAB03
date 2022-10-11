# Laboratorium - synchronizacja

Tematem zajęć jest synchronizacja wątków za pomocą semaforów, barier i zasuwek z pakietu `java.util.concurrent`.

Do scenariusza dołączone są programy przykładowe:

- [TwoIncreasers.java](https://github.com/Emilo77/SEM5-PW-LAB03/blob/master/TwoCounterIncreasers.java)

- [ProducersConsumers.java](https://github.com/Emilo77/SEM5-PW-LAB03/blob/master/ProducersConsumers.java)

- [ArrayRearrangement.java](https://github.com/Emilo77/SEM5-PW-LAB03/blob/master/ArrayRearrangement.java)

- [CountDown.java](https://github.com/Emilo77/SEM5-PW-LAB03/blob/master/CountDown.java)

- [MatrixRowSums.java](https://github.com/Emilo77/SEM5-PW-LAB03/blob/master/MatrixRowSums.java)

### Motywacja 

Do tej pory do synchronizacji pracy wątków wykorzystywaliśmy aktywne oczekiwanie, które – jak już zapowiadaliśmy wcześniej – nie jest optymalnym rozwiązaniem. Na dzisiejszych zajęciach poznamy 3 wysokopoziomowe konstrukcje pozwalające na synchronizację wielu wątków w różny sposób, ale bez aktywnego oczekiwania.

### Semafor (`Semaphore`)

Implementacja semafora jest w klasie [Semaphore](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/Semaphore.html).

W dokumentacji semafor jest opisany jako obiekt posiadający pewną liczbę pozwoleń, które udostępnia wątkom.

Klasa `Semaphore` ma konstruktor `Semaphore(int num)` tworzący semafor, który początkowo dysponuje `num` pozwoleniami.

Metoda `acquire()` odpowiada klasycznej operacji `P()` na semaforze. Wykonujący ją wątek pobiera pozwolenie od semafora, o ile jest ono dostępne. Jeżeli żadnego pozwolenia nie ma, wątek czeka na nie.

Metoda `release()` odpowiada klasycznej operacji `V()` na semaforze. Budzi wątek oczekujący na tym semaforze na pozwolenie lub zwiększa o jeden liczbę pozwoleń semafora, jeśli żaden wątek nie czeka.

Semafor z jednym pozwoleniem może być wykorzystywany do rozwiązania problemu sekcji krytycznej, znanego również pod nazwą problemu wzajemnego wykluczania. Taki semafor nazywany jest muteksem (ang. *MUTual EXclusion*).

Implementacja nie daje gwarancji uczciwości semafora zbudowanego konstruktorem `Semaphore(int num)`. Wątek oczekujący na operacji `acquire()` może zostać zagłodzony.

W klasie `Semaphore` jest alternatywny konstruktor, z drugim argumentem typu `boolean`. Wartość true tego argumentu wskazuje, że wątki mają oczekiwać na pozwolenia w kolejce.

Program [TwoCounterIncreasers.java](https://github.com/Emilo77/SEM5-PW-LAB03/blob/master/TwoCounterIncreasers.java) demonstruje rozwiązanie problemu sekcji krytycznej.

Zastosowanie konstrukcji `try { ... } finally { ... }` gwarantuje, że operacja `release()` zostanie wykonana nawet w przypadku przerwania wątku w sekcji krytycznej.

Program [ProducersConsumers.java](https://github.com/Emilo77/SEM5-PW-LAB03/blob/master/ProducersConsumers.java) demonstruje rozwiązanie problemu producentów i konsumentów.

### Bariera (`CyclicBarrier`)

Bariera jest narzędziem synchronizacyjnym wstrzymującym grupę wątków do chwili, gdy wszystkie osiągną wyznaczone miejsce w kodzie.

Implementacja bariery w klasie [CyclicBarrier](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/CyclicBarrier.html) ma konstruktor `CyclicBarrier(int parties)` budujący barierę synchronizującą grupę wątków o liczności `parties`.

Bariera ma metodę `await()` sygnalizującą, że wątek osiągnął barierę.

W klasie `CyclicBarrier` jest też konstruktor `CyclicBarrier(int parties, Runnable barrierAction)`. Buduje on barierę wykonującą akcję `barrierAction` po przybyciu ostatniego wątku, ale przed zwolnieniem wszystkich.

Ten sam obiekt klasy `CyclicBarrier` może być użyty jako bariera wielokrotnie (stąd w nazwie cykliczność). Po zwolnieniu grupy oczekujących wątków, synchronizuje kolejną.

Jeżeli któryś z wątków czekających na barierze zostanie przerwany, pozostałym zgłaszany jest kontrolowany wyjątek klasy `BrokenBarrierException`.

Program [ArrayRearrangement.java](https://github.com/Emilo77/SEM5-PW-LAB03/blob/master/ArrayRearrangement.java) demonstruje zastosowanie barier.

### Zasuwka (`CountDownLatch`)

Bariera wstrzymuje wątek w oczekiwaniu na inne wątki. Innym narzędziem synchronizacyjnym jest zasuwka, która realizuje oczekiwanie na określoną liczbę zdarzeń.

Zasuwka jest zaimplementowana w klasie [CountDownLatch](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/CountDownLatch.html). Jej konstruktor `CountDownLatch(int count)` buduje zasuwkę wstrzymującą wątek w oczekiwaniu na `count` zdarzeń.

Metoda `countDown()` generuje jedno zdarzenie a metoda `await()` wstrzymuje wątek na zasuwce.

Program [CountDown.java](https://github.com/Emilo77/SEM5-PW-LAB03/blob/master/CountDown.java) demonstruje zastosowanie zasuwki. Wynik programu nigdy nie będzie mniejszy niż liczba `ITERATIONS_BEFORE_WAKEUP`, ale może być od tej wartości większy.

### Ćwiczenie punktowane (MatrixRowSumsConcurrent)

Dany jest program `MatrixRowSums.java`.

Mamy daną klasę `Matrix`, która reprezentuje macierz o zadanej wielkości, której elementy oblicza funkcja definition na podstawie numeru wiersza i kolumny.

Metoda `int[] rowSums()` zwraca tablicę wypełnioną sumami elementów w wierszach macierzy. Metoda ta oblicza kolejne elementy macierzy sekwencyjnie.

Zakładamy, że wykonanie operacji `definition` może być kosztowne. Chcemy umożliwić współbieżne liczenie wielu elementów.

### Polecenie

Zaimplementuj w klasie `Matrix` analogiczną metodę `int[] rowSumsConcurrent()`, w której sumywszystkich elementów z tego samego wiersza macierzy będą liczone współbieżnie.

Wątki synchronizuj za pomocą semaforów, barier lub zasuwek.

### Wskazówka

Należy utworzyć tyle wątków pomocniczych, ile kolumn ma macierz. Każdy wątek będzie liczył elementy ze swojej kolumny we wszystkich wierszach.

Po wyznaczeniu wszystkich elementów wiersza, liczymy i wpisujemy do tablicy ich sumę, po czym przechodzimy do kolejnego wiersza.

Poprawność możesz sprawdzić, porównując tablicę wyznaczoną przez wersję współbieżną i sekwencyjną.
