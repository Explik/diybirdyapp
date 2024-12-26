export interface TextContent {
    text: string
}

export interface FlashcardContent {
    front: TextContent | undefined
    back: TextContent | undefined
}