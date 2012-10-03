package demo.javaone.camelcoherence

import java.util.concurrent.ArrayBlockingQueue

class FifoBlockingQueue[A](size: Int) extends ArrayBlockingQueue[A](size) {
  override def offer(a: A) = {
    if (remainingCapacity() == 0) poll()
    super.offer(a)
  }
}