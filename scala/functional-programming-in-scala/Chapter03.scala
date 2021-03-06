import scala.annotation.tailrec

object Chapter03 {

  import List._

  def main(args: Array[String]): Unit = {
    val arr123 = List(1, 2, 3)
    val arr321 = List(3, 2, 1)
    val arr12345 = List(1, 2, 3, 4, 5)

    println(tail(arr123) == List(2, 3))
    println(setHead(arr123, 2) == List(2, 2, 3))
    println(drop(arr123, 2) == List(3))
    println(dropWhile(arr12345, (a: Int) => a < 4) == List(4, 5))
    println(init(arr12345) == List(1, 2, 3, 4))
    println(length(arr12345) == 5)
    println(foldLeft(arr123, Nil: List[Int])((z, a) => Cons(a, z)) == arr321)
    println(reverse(List("a", "b", "c")) == List("c", "b", "a"))
    println(
      foldLeftByRight(arr123, Nil: List[Int])((z, a) => Cons(a, z)) == arr321
    )
    println(foldRightByLeft(arr123, Nil: List[Int])(Cons(_, _)) == arr123)
    println(append(List(1, 2, 3), List(4, 5, 6)) == List(1, 2, 3, 4, 5, 6))
    println(
      flatten(List(List(1, 2, 3), List(4, 5, 6))) == List(1, 2, 3, 4, 5, 6)
    )

    println(hasSubsequence(List(1,2,3,4), List(1,2)))
    println(hasSubsequence(List(1,2,3,4), List(2,3)))
    println(hasSubsequence(List(1,2,3,4), List(2,3,4)))
    println(hasSubsequence(List(1,2,3,4), List(1,2,3,4)))
    println(hasSubsequence(List(1,2,3,4), List(1,2,3)))
    println(hasSubsequence(List(1,2,3,4), List(4)))
  }

}

sealed trait List[+A]

case object Nil extends List[Nothing]

case class Cons[+A](head: A, tail: List[A]) extends List[A]

object List {

  // 3.2
  def tail[A](as: List[A]): List[A] = as match {
    case Nil        => Nil
    case Cons(_, t) => t
  }

  // 3.3
  def setHead[A](as: List[A], newHead: A): List[A] = as match {
    case Nil        => Nil
    case Cons(_, t) => Cons(newHead, t)
  }

  // 3.4
  @tailrec
  def drop[A](l: List[A], n: Int): List[A] = l match {
    case Cons(_, t) if n > 0 => drop(t, n - 1)
    case _                   => l
  }

  // 3.5
  @tailrec
  def dropWhile[A](l: List[A], f: A => Boolean): List[A] = l match {
    case Cons(h, t) if f(h) => dropWhile(t, f)
    case _                  => l
  }

  // 3.6
  def init[A](l: List[A]): List[A] = l match {
    case Nil                      => Nil
    case Cons(_, t) if (t == Nil) => Nil
    case Cons(h, t)               => Cons(h, init(t))
  }

  // 3.7
  def product(ds: List[Double]): Double = foldRight(ds, 0.0)(_ * _)

  // 3.8
  // f(1, f(2, f(3, z))) => f = Cons => List(1,2,3)

  // 3.9
  def length[A](as: List[A]): Int = as match {
    case Nil        => 0
    case Cons(_, t) => 1 + length(t)
  }

  // 3.10
//  foldLeft(arr123, Nil: List[Int])((z, a) => Cons(a, z)) == arr321
  @tailrec
  def foldLeft[A, B](l: List[A], z: B)(f: (B, A) => B): B = l match {
    case Nil        => z
    case Cons(h, t) => foldLeft(t, f(z, h))(f)
  }

  // 3.12
  def reverse[A](l: List[A]): List[A] =
    foldLeft(l, Nil: List[A])((z, a) => Cons(a, z))

  // 3.13.1
  def foldLeftByRight[A, B](l: List[A], z: B)(f: (B, A) => B): B =
    foldRight(l, (b: B) => b)((a, g) => b => g(f(b, a)))(z)

  // 3.13.2
  def foldRightByLeft[A, B](l: List[A], z: B)(f: (A, B) => B): B =
    foldLeft(l, (b: B) => b)((g, a) => b => g(f(a, b)))(z)

  // 3.14
  def append[A](l: List[A], r: List[A]): List[A] = l match {
    case Nil        => r
    case Cons(h, t) => Cons(h, append(t, r))
  }

  // 3.15
  def flatten[A](l: List[List[A]]): List[A] =
    foldRight(l, Nil: List[A])((a, z) => append(a, z))

  // 3.16
  def plusOne(l: List[Int]): List[Int] =
    foldRight(l, Nil: List[Int])((h, z) => Cons(h + 1, z))

  // 3.17
  def doubleToString(l: List[Double]): List[String] =
    foldRight(l, Nil: List[String])((h, z) => Cons(h.toString, z))

  // 3.18
  def map[A, B](as: List[A])(f: A => B): List[B] =
    foldRight(as, Nil: List[B])((h, z) => Cons(f(h), z))

  // 3.19
  def filter[A](as: List[A])(f: A => Boolean): List[A] =
    foldRight(as, Nil: List[A])((h, z) => if (f(h)) Cons(h, z) else z)

  // 3.20
  def flatMap[A, B](as: List[A])(f: A => List[B]): List[B] =
    foldRight(as, Nil: List[B])((h, z) => append(f(h), z))

  // 3.21
  def filterWithFlatMap[A](as: List[A])(f: A => Boolean): List[A] =
    flatMap(as)((e) => if (f(e)) List(e) else Nil)

  // 3.22
  def pairwiseSum(l: List[Int], r: List[Int]): List[Int] = (l, r) match {
    case (Nil, Nil)                   => Nil
    case (Cons(lh, lt), Cons(rh, rt)) => Cons(lh + rh, pairwiseSum(lt, rt))
  }

  // 3.23
  def zipWith[A, B](l: List[A], r: List[A])(f: (A, A) => B): List[B] =
    (l, r) match {
      case (Nil, Nil)                   => Nil
      case (Cons(lh, lt), Cons(rh, rt)) => Cons(f(lh, rh), zipWith(lt, rt)(f))
    }

  // 3.24  - wrong version
  // f((1,2,3,4), (1,3,4)) => true, because it substitute to,
  // f((2,3,4), (3,4)) => f((3,4), (3,4))
  // should remember sub when we find the unmatched character.
  def hasSubsequenceWrong[A](sup: List[A], sub: List[A]): Boolean = (sup, sub) match {
    case (Nil, Nil) => true
    case (Nil, _) => false
    case (_, Nil) => true
    case (Cons(ph, pt), Cons(bh, bt)) if ph == bh => hasSubsequence(pt, bt)
    case (Cons(_, pt), Cons(_, _))  => hasSubsequence(pt, sub)
  }

// 3.24
  @tailrec
  def startsWith[A](sup: List[A], sub: List[A]): Boolean = (sup, sub) match {
    case (_, Nil) => true
    case (Nil, _) => false
    case (Cons(supH, supT), Cons(subH, subT)) =>
      supH == subH && startsWith(supT, subT)
  }

  @tailrec
  def hasSubsequence[A](sup: List[A], sub: List[A]): Boolean =
    (sup, sub) match {
      case (Nil, _)        => false
      case (Cons(_, t), _) => startsWith(sup, sub) || hasSubsequence(t, sub)
    }

  def sum(ints: List[Int]): Int = foldRight(ints, 0)((a, b) => a + b)

  def foldRight[A, B](as: List[A], z: B)(f: (A, B) => B): B = as match {
    case Nil        => z
    case Cons(h, t) => f(h, foldRight(t, z)(f))
  }

  def apply[A](as: A*): List[A] = {
    if (as.isEmpty) Nil
    else Cons(as.head, apply(as.tail: _*))
  }

}

sealed trait Tree[+A]
case class Leaf[A](value: A) extends Tree[A]
case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]

object Tree {

  // 3.25
  def size[A](tree: Tree[A]): Int = tree match {
    case Leaf(_)             => 1
    case Branch(left, right) => 1 + size(left) + size(right)
  }

  // 3.26
  def maximum(tree: Tree[Int]): Int = tree match {
    case Leaf(v) => v
    case Branch(left, right) =>
      (maximum(left), maximum(right)) match {
        case (leftMax, rightMax) =>
          if (leftMax > rightMax) leftMax else rightMax
      }
  }

  // 3.27
  def depth[A](tree: Tree[A]): Int = tree match {
    case Leaf(_) => 1
    case Branch(left, right) =>
      (depth(left), depth(right)) match {
        case (leftDepth, rightDept) =>
          (if (leftDepth > rightDept) leftDepth else rightDept) + 1
      }
  }

  // 3.28
  def mapTree[A, B](tree: Tree[A])(f: A => B): Tree[B] = tree match {
    case Leaf(v)             => Leaf(f(v))
    case Branch(left, right) => Branch(mapTree(left)(f), mapTree(right)(f))
  }

  // 3.29
  // Can you draw an analogy between this fold function and the left and right folds for List?
  def foldTree[A, B](tree: Tree[A], z: B)(f: (A, B) => B)(g: (B, B) => B): B =
    tree match {
      case Leaf(v) => f(v, z)
      case Branch(left, right) =>
        g(foldTree(left, z)(f)(g), foldTree(right, z)(f)(g))
    }

  def sizeWithFold[A](tree: Tree[A]): Int =
    foldTree(tree, 0)((_, z) => z + 1)(_ + _ + 1)

  def maximumWithFold(tree: Tree[Int]): Int =
    foldTree(tree, Int.MinValue)((a, z) => if (a > z) a else z)(
      (l, r) => if (l > r) l else r
    )

  def depthWithFold[A](tree: Tree[A]): Int =
    foldTree(tree, 0)((_, z: Int) => z + 1)((l, r) => (if (l > r) l else r) + 1)
}
