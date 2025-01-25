import { AudioContent } from "./editAudioContent.model";

export interface EditFlashcard {
    id: string;

    leftContentType: "text" | "image" | "audio";
    rightContentType: "text" | "image" | "audio";

    leftTextContent?: EditFlashcardText;
    rightTextContent?: EditFlashcardText;

    leftImageContent?: EditFlashcardImage;
    rightImageContent?: EditFlashcardImage;

    leftAudioContent?: AudioContent;
    rightAudioContent?: AudioContent;
}

export interface EditFlashcardText {
    id: string;
    text: string;
    languageId?: string; 
}

export interface EditFlashcardImage {
    id: string;
    imageUrl?: string;
    imageData?: string;

    getDataUrl(): string;
}

export interface EditFlashcardAudio {
    id: string;
    audioUrl?: string;
    audioData?: string;
}

// View-model class for editing a flashcard
export class EditFlashcardImpl implements EditFlashcard {
    id: string = "unknown";
    leftContentType: "text" | "image" | "audio" = "text";
    rightContentType: "text" | "image" | "audio" = "text";

    leftTextContent?: EditFlashcardTextImpl;
    rightTextContent?: EditFlashcardTextImpl;

    leftImageContent?: EditFlashcardImage;
    rightImageContent?: EditFlashcardImage;

    leftAudioContent?: AudioContent;
    rightAudioContent?: AudioContent;

    private constructor() {
        
    }

    public duplicate(): EditFlashcardImpl {
        throw new Error("Not implemented");
    }

    public static create(): EditFlashcardImpl {
        var flashcard = new EditFlashcardImpl();
        // TODO add defaults
        return flashcard;
    }

    public static createFromDto(dto: FlashcardDto): EditFlashcardImpl {
        var flashcard = new EditFlashcardImpl();
        flashcard.id = dto.id;

        flashcard.leftContentType = "text";
        flashcard.rightContentType = "text";

        flashcard.leftTextContent = new EditFlashcardTextImpl(dto.leftValue);
        flashcard.rightTextContent = new EditFlashcardTextImpl(dto.rightValue);

        return flashcard;
    }
}

export class EditFlashcardTextImpl implements EditFlashcardText {
    _text: string = "";
    _languageId?: string = undefined;

    id: string = "unknown"; 
    isDirty: boolean = false;

    constructor(textValue: string) {
        this._text = textValue;
    }

    set text(value: string) {
        if (this._text !== value) {
            this._text = value;
            this.isDirty = true;
        }
    }

    get text(): string {
        return this._text;
    }

    set languageId(value: string | undefined) {
        if (this._languageId !== value) {
            this._languageId = value;
            this.isDirty = true;
        }
    }

    get languageId(): string | undefined {
        return this._languageId;
    }
} 

export class EditFlashcardImageImpl implements EditFlashcardImage {
    id: string = "unknown";
    imageUrl?: string;
    imageData?: string;

    getDataUrl(): string {
        return this.imageUrl ?? this.imageData ?? "";
    }

    static createFromFile(file: File): EditFlashcardImageImpl {
        var image = new EditFlashcardImageImpl();
        image.imageUrl = URL.createObjectURL(file);
        return image;
    }

    static createFromUrl(url: string): EditFlashcardImageImpl {
        var image = new EditFlashcardImageImpl();
        image.imageUrl = url;
        return image;
    }
}