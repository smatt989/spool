export const triggerModel = {
    events: [
        {
            title: "adventure starts",
            id: 1
        },
        {
            title: "time of day changes",
            disabled: true,
            id: 2
        },
        {
            title: "player location changes",
            disabled: true,
            id: 3
        },
        {
            title: "player enters location",
            id: 4,
            disabled: true,
            variables: [
                {
                     name: "location",
                     variableArity: "one",
                     variableType: "object"
                }
            ]
        },
        {
            title: "player exits location",
            id: 5,
            disabled: true,
            variables: [
                {
                     name: "location",
                     variableArity: "one",
                     variableType: "object"
                }
            ]
        },
        {
            title: "periodic",
            id: 6,
            disabled: true,
            variables: [
                {
                     name: "seconds",
                     variableArity: "one",
                     variableType: "integer"
                }
            ]
        }
    ],
    conditions: [
        {
            title: "and",
            id: 7,
            disabled: true
        },
        {
            title: "or",
            id: 8,
            disabled: true
        },
        {
            title: "not",
            id: 9,
            disabled: true
        }
    ],
    comparisons: [
        {
            title: "equals",
            id: 10,
            disabled: true
        },
        {
            title: "not equal",
            id: 11,
            disabled: true
        },
        {
            title: "greater than",
            id: 12,
            disabled: true
        },
        {
            title: "greater than or equal",
            id: 13,
            disabled: true
        },
        {
            title: "less than",
            id: 14,
            disabled: true
        },
        {
            title: "less than or equal",
            id: 15,
            disabled: true
        }
    ],
    actions: [
        {
            title: "direct player to location",
            id: 16,
            variables: [
                {
                    name: "destination",
                    variableArity: "one",
                    variableType: "object"
                }
            ]
        },
        {
            title: "direct player to location with ordered waypoints",
            id: 17,
            variables: [
                {
                    name: "destination",
                    variableArity: "one",
                    variableType: "object"
                },
                {
                    name: "waypoints",
                    variableArity: "array",
                    variableType: "object"
                }
            ]
        },
        {
            title: "direct player to location with waypoints optimized",
            id: 18,
            disabled: true,
            variables: [
                {
                    name: "destination",
                    variableArity: "one",
                    variableType: "object"
                },
                {
                    name: "waypoints",
                    variableArity: "array",
                    variableType: "object"
                }
            ]
        },
        {
            title: "pause directions",
            id: 19,
            disabled: true
        },
        {
            title: "resume directions",
            id: 20,
            disabled: true
        },
        {
            title: "end directions",
            id: 21,
            disabled: true
        },
        {
            title: "move object to",
            id: 22,
            disabled: true,
            variables: [
                {
                    name: "object",
                    variableArity: "one",
                    variableType: "object"
                },
                {
                    name: "location",
                    variableArity: "one",
                    variableType: "object"
                }
            ]
        },
        {
            title: "make object visible",
            id: 23,
            disabled: true,
            variables: [
                {
                    name: "object",
                    variableArity: "one",
                    variableType: "object"
                }
            ]
        },
        {
            title: "make object hidden",
            id: 24,
            disabled: true,
            variables: [
                {
                     name: "object",
                     variableArity: "one",
                     variableType: "object"
                }
            ]
        },
        {
            title: "destroy object",
            id: 25,
            disabled: true,
            variables: [
                {
                     name: "object",
                     variableArity: "one",
                     variableType: "object"
                }
            ]
        },
        {
            title: "end adventure",
            id: 26,
            disabled: true
        },
        {
            title: "victory",
            id: 27,
            disabled: true
        },
        {
            title: "defeat",
            id: 28,
            disabled: true
        }
    ]

};