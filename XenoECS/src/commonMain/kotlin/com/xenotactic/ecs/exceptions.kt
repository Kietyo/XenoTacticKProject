package com.xenotactic.ecs

abstract class ECSException(
        messageFn: () -> String
): RuntimeException(messageFn())

class ECSEntityDoesNotExist(messageFn: () -> String) : ECSException(messageFn)
class ECSComponentNotFoundException(messageFn: () -> String) : ECSException(messageFn)
class ECSComponentAlreadyExistsException(messageFn: () -> String) : ECSException(messageFn)

class SingletonInjectionAlreadyExistsException(messageFn: () -> String) : ECSException(messageFn)
class SingletonInjectionDoesNotExistException(messageFn: () -> String) : ECSException(messageFn)