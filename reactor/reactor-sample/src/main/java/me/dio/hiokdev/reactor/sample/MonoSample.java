package me.dio.hiokdev.reactor.sample;

import reactor.core.publisher.Mono;

import java.util.Optional;

public class MonoSample {

    public static void main(String[] args) {
        Mono.just(1)
                .doOnSuccess(System.out::println)
                .subscribe(); // 1

        Mono.justOrEmpty(2)
                .doOnSuccess(System.out::println)
                .subscribe(); // 2

//        Mono.just(null)
//                .doOnSuccess(System.out::println)
//                .subscribe(); // java.lang.NullPointerException

        Mono.justOrEmpty(Optional.empty())
                .doOnSuccess(System.out::println)
                .subscribe(); // null

        Mono.justOrEmpty(Optional.empty())
                .defaultIfEmpty(0)
                .doOnSuccess(System.out::println)
                .subscribe(); // 0

        Mono.justOrEmpty(2)
                .defaultIfEmpty(0)
                .doOnSuccess(System.out::println)
                .subscribe(); // 2

        Mono.justOrEmpty(2)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new Exception("Mono error"))))
                .doOnSuccess(System.out::println)
                .subscribe(); // 2

//        Mono.justOrEmpty(Optional.empty())
//                .switchIfEmpty(Mono.defer(() -> Mono.error(new Exception("Mono error"))))
//                .doOnSuccess(System.out::println)
//                .subscribe(); // java.lang.Exception: Mono error

        Mono.justOrEmpty(2)
                .map(n -> n * n)
                .doOnSuccess(System.out::println)
                .subscribe(); // 4

        Mono.justOrEmpty(2)
                .map(String::valueOf)
                .doOnSuccess(System.out::println)
                .subscribe(); // 2

        Mono.justOrEmpty(2)
                .map(n -> String.valueOf(n).getClass())
                .doOnSuccess(System.out::println)
                .subscribe(); // class java.lang.String

        Mono.justOrEmpty(2)
                .filter(n -> n % 2 == 0)
                .doOnSuccess(System.out::println)
                .subscribe(); // 2

        Mono.justOrEmpty(3)
                .filter(n -> n % 2 == 0)
                .doOnSuccess(System.out::println)
                .subscribe(); // null

        Mono.justOrEmpty(Optional.of(2))
                .filter(n -> n % 2 == 0)
                .defaultIfEmpty(0)
                .doOnSuccess(System.out::println)
                .subscribe(); // 2

        Mono.justOrEmpty(Optional.of(3))
                .filter(n -> n % 2 == 0)
                .defaultIfEmpty(0)
                .doOnSuccess(System.out::println)
                .subscribe(); // 0
    }

}
