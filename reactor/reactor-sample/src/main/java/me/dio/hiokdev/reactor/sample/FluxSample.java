package me.dio.hiokdev.reactor.sample;

import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FluxSample {

    public static void main(String[] args) {
        Flux.fromIterable(List.of(1, 2, 3, 4, 5, 6))
                .doOnNext(System.out::println)
                .subscribe(); // 1\n 2\n 3\n 4\n 5\n 6\n

        Flux.fromIterable(List.of(1, 2, 3, 4, 5, 6))
                .map(n -> n * 10)
                .doOnNext(System.out::println)
                .subscribe(); // 10\n 20\n 30\n 40\n 50\n 60\n

        Flux.fromIterable(List.of(1, 2, 3, 4, 5, 6))
                .map(n -> n * 10)
                .collectList()
                .doOnNext(System.out::println)
                .subscribe(); // [10, 20, 30, 40, 50, 60]

        Flux.fromIterable(List.of(1, 2, 3, 4, 5, 6))
                .map(n -> n * 15)
                .filter(n -> n % 2 == 0)
                .doOnNext(System.out::println)
                .subscribe(); // 30\n 60\n 90\n

        Flux.fromIterable(List.of(1, 2, 3, 4, 5, 6))
                .map(n -> n * 15)
                .doOnNext(System.out::println)
                .filter(n -> n % 2 == 0)
                .doOnNext(System.out::println)
                .subscribe(); // 15\n 30\n 30\n 45\n 60\n 60\n 75\n 90\n 90\n

        Flux.fromIterable(List.of(1, 2, 3, 4, 5, 6))
                .map(n -> Map.of(n.getClass(), n))
                .doOnNext(System.out::println)
                .subscribe(); // {class java.lang.Integer=1}\n {class java.lang.Integer=2}\n ... {class java.lang.Integer=6}\n

        Flux.fromIterable(List.of(1, 2, 3, 4, 5, 6))
                .map(n -> Map.of(n.getClass(), n))
                .collectList()
                .doOnNext(System.out::println)
                .subscribe(); // [{class java.lang.Integer=1}, {class java.lang.Integer=2}, ... {class java.lang.Integer=6}\n]

        Flux.fromIterable(List.of(1, 2, 3, 4, 5, 6))
                .map(n -> Map.of(n.getClass().toString(), n))
                .collectList()
                .map(list -> {
                    var newMap = new HashMap<>();
                    list.forEach(line -> newMap.put("class java.lang.Integer" + line.get("class java.lang.Integer"),
                            line.get("class java.lang.Integer")));
                    return newMap;
                })
                .doOnNext(System.out::println)
                .subscribe(); // {class java.lang.Integer1=1, class java.lang.Integer6=6, ..., class java.lang.Integer2=2}

//        Flux.fromIterable(null)
//                .doOnNext(System.out::println)
//                .subscribe(); // NullPointerException

        Flux.empty()
                .doOnNext(System.out::println)
                .subscribe(); //
    }

}
