Object.defineProperty(globalThis,"window",{
    value: globalThis,
    configurable: false
})

Object.defineProperty(globalThis,"global",{
    value: globalThis,
    configurable: false
});

Object.defineProperty(globalThis,"self",{
    value: globalThis,
    configurable: false
})

import "./loader"
import "./timer"
import "./ws"