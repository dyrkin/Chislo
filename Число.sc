object Цифра {

  object Ноль extends Цифра {override val значение = 0; override def toString = "Ноль"}
  object Один extends Цифра {override val значение = 1; override def toString = "Один"}
  object Два extends Цифра {override val значение = 2; override def toString = "Два"}
  object Три extends Цифра {override val значение = 3; override def toString = "Три"}
  object Четыре extends Цифра {override val значение = 4; override def toString = "Четыре"}
  object Пять extends Цифра {override val значение = 5; override def toString = "Пять"}
  object Шесть extends Цифра {override val значение = 6; override def toString = "Шесть"}
  object Семь extends Цифра {override val значение = 7; override def toString = "Семь"}
  object Восемь extends Цифра {override val значение = 8; override def toString = "Восемь"}
  object Девять extends Цифра {override val значение = 9; override def toString = "Девять"}
  
  type ЦелоеЧисло = Int
  type Список[+Тип]= List[Тип]
  type Цифры = Список[Цифра]
  type Любое = Any
  type Логическое = Boolean
  type Строка = String
  type Возможное[+Тип] = Option[Тип]

  object Чтото {
    def apply[Тип](значение: => Тип) = Some(значение)
  }

  object Ничего {
    def apply() = None
  }
  
  object Список {
    def apply[Тип](элементы: Тип*): Список[Тип] = элементы.toList
  }
  
  object система {
    def ошибка(сообщение: Строка) = sys.error(сообщение) 
  }

  val всеЦифрыСИндексом = Список(Ноль, Один, Два, Три, Четыре, Пять, Шесть, Семь, Восемь, Девять).zipWithIndex
  
  implicit class БогатоеЦелоеЧисло(значение1: ЦелоеЧисло) {
    def делитьНа(значение2: ЦелоеЧисло) = значение1 / значение2
    def взятьОстатокОтДеленияНа(значение2: ЦелоеЧисло) = значение1 % значение2
  }

  implicit class БогатоеВозможное[Тип](возможноеЗначение: Возможное[Тип]) {
    def взятьЭтоИначеТо(то: => Тип): Тип = возможноеЗначение.getOrElse(то)
  }

  implicit class БогатыйСписок[Тип](список1: Список[Тип]) {
    def объеденитьС(список2: Список[Тип]) = список1 ::: список2
  }

  implicit class БогатоеЛюбое(переменная1: Любое) {
    def соответствует(переменная2: Любое): Boolean = переменная1 == переменная2
  }

  implicit class БогатоеЛогическое(переменная1: Логическое) {
    def и(переменная2: Логическое) = переменная1 && переменная2
    def или(переменная2: Логическое) = переменная1 || переменная2
  }

  object Иначе {
    def apply[Тип](функция: => Возможное[Тип]) = new Иначе[Тип](функция)
  }

  class Иначе[Тип](val если: Возможное[Тип]){
    def иначе(функция: => Тип): Тип = если.взятьЭтоИначеТо(функция)
  }

  def если[Тип](условие: => Логическое)(функция: => Тип): Иначе[Тип] = {
    if(условие) Иначе(Чтото(функция)) else Иначе(Ничего())
  }

  trait Цифра {
    val значение: ЦелоеЧисло

    def значениВЦифру: Цифра = всеЦифрыСИндексом.find{case (_, индекс) => индекс == значение}.взятьЭтоИначеТо(система.ошибка(s"Неопознанная цифра: $значение"))._1

    override def toString = значениВЦифру.toString
  }

  class Число(val значение: ЦелоеЧисло) extends Цифра {
    def значениеВЦифры(первое: ЦелоеЧисло, второе: ЦелоеЧисло, результат: Цифры = Список()): Цифры = {
      если((первое соответствует 0)  и (второе соответствует 0)){
        результат
      } иначе{
        значениеВЦифры(первое делитьНа 10, первое взятьОстатокОтДеленияНа 10, Список(
          new Цифра{override val значение = второе}
        ) объеденитьС результат)
      }
    }

    def значениеВЦифры: Цифры = {
      если(значение.делитьНа(10).соответствует(0) и значение.взятьОстатокОтДеленияНа(10).соответствует(0)) {
        Список[Цифра](Ноль)
      } иначе {
        значениеВЦифры(значение делитьНа 10, значение взятьОстатокОтДеленияНа 10)
      }
    }
    override def toString = значениеВЦифры.mkString
  }

  implicit class Числовой(число: Цифра) {
    def умножитьНа(число2: Цифра) = new Число(число.значение * число2.значение)
    def соотвествует(число2: Цифра) = число.значение соответствует число2.значение
  }


  //Тесты
  val десять = Пять умножитьНа Два // = ОдинНоль
  val ответ1 = (Пять умножитьНа Два) соотвествует (Два умножитьНа Пять) // = true
  val двадцатьПять = Пять умножитьНа Пять // = ДваПять
  val ответ2 = (Пять умножитьНа Пять) соотвествует (Пять умножитьНа Пять) // = true
  val девяносто = (Два умножитьНа Пять) умножитьНа Девять // = ДевятьНоль
  val ответ3 = ((Два умножитьНа Пять) умножитьНа Девять) соотвествует ((Девять умножитьНа Пять) умножитьНа Два) // = true

}