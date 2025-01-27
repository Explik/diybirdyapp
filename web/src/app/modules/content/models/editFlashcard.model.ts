// =================================================================================================
// ViewModel interfaces 
// =================================================================================================
export interface EditFlashcardDeck extends Partial<FlashcardDeckDto> {
    get name(): string;
    get flashcards(): EditFlashcard[];
}

export interface EditFlashcardLanguage extends Partial<FlashcardLanguageDto> {

}

export interface EditFlashcard extends Partial<FlashcardDto> {
    leftContentType: "text" | "image" | "audio" | "video";
    rightContentType: "text" | "image" | "audio" | "video";

    leftTextContent?: EditFlashcardText;
    rightTextContent?: EditFlashcardText;

    leftImageContent?: EditFlashcardImage;
    rightImageContent?: EditFlashcardImage;

    leftAudioContent?: EditFlashcardAudio;
    rightAudioContent?: EditFlashcardAudio;

    leftVideoContent?: EditFlashcardVideo;
    rightVideoContent?: EditFlashcardVideo;
}

export interface EditFlashcardText extends Partial<FlashcardContentTextDto> {

}

export interface EditFlashcardImage extends Partial<FlashcardContentImageDto> {
    getSrc(): string;
}

export interface EditFlashcardAudio extends Partial<FlashcardContentAudioDto> {
    generateElement(): HTMLAudioElement;
    generateBlob(): Blob;
}

export interface EditFlashcardVideo extends Partial<FlashcardContentVideoDto> {
    getSrc(): string;
}

// =================================================================================================
// ViewModel implementations
// =================================================================================================
export class EditFlashcardDeckImpl implements EditFlashcardDeck {
    id: string = "unknown";
    name: string = "unknown";
    flashcards: EditFlashcard[] = [];

    static createFromDto(dto: FlashcardDeckDto): EditFlashcardDeckImpl {
        var deck = new EditFlashcardDeckImpl();
        deck.id = dto.id;
        deck.name = dto.name;
        //deck._flashcards = dto.map(EditFlashcardImpl.createFromDto);
        return deck;
    }
}

export class EditFlashcardLanguageImpl implements EditFlashcardLanguage {
    id: string = "unknown";
    name: string = "unknown";
    abbreviation: string = "unknown";

    static createFromDto(dto: FlashcardLanguageDto): EditFlashcardLanguageImpl {
        var language = new EditFlashcardLanguageImpl();
        language.id = dto.id;
        language.name = dto.name;
        language.abbreviation = dto.abbreviation;
        return language;
    }
}

export class EditFlashcardImpl implements EditFlashcard {
    id: string = "unknown";
    leftContentType: "text" | "image" | "audio" | "video" = "text";
    rightContentType: "text" | "image" | "audio" | "video" = "text";

    leftTextContent?: EditFlashcardTextImpl;
    rightTextContent?: EditFlashcardTextImpl;

    leftImageContent?: EditFlashcardImage;
    rightImageContent?: EditFlashcardImage;

    leftAudioContent?: EditFlashcardAudio;
    rightAudioContent?: EditFlashcardAudio;

    leftVideoContent?: EditFlashcardVideo | undefined;
    rightVideoContent?: EditFlashcardVideo | undefined;

    private constructor() { }

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

        switch(dto.frontContent.type) {
            case "audio":
                flashcard.leftContentType = "audio";
                flashcard.leftAudioContent = EditFlashcardAudioImpl.createFromDto(<FlashcardContentAudioDto>dto.frontContent);
                break;
            case "text": 
                flashcard.leftContentType = "text";
                flashcard.leftTextContent = EditFlashcardTextImpl.createFromDto(<FlashcardContentTextDto>dto.frontContent);
                break;
            case "image":
                flashcard.leftContentType = "image";
                flashcard.leftImageContent = EditFlashcardImageImpl.createFromDto(<FlashcardContentImageDto>dto.frontContent);
                break; 
            case "video":
                flashcard.leftContentType = "video";
                flashcard.leftVideoContent = EditFlashcardVideoImpl.createFromDto(<FlashcardContentVideoDto>dto.frontContent);
                break;
        }

        switch (dto.backContent.type) {
            case "audio":
                flashcard.rightContentType = "audio";
                flashcard.rightAudioContent = EditFlashcardAudioImpl.createFromDto(<FlashcardContentAudioDto>dto.backContent);
                break;
            case "text": 
                flashcard.rightContentType = "text";
                flashcard.rightTextContent = EditFlashcardTextImpl.createFromDto(<FlashcardContentTextDto>dto.backContent);
                break;
            case "image":
                flashcard.rightContentType = "image";
                flashcard.rightImageContent = EditFlashcardImageImpl.createFromDto(<FlashcardContentImageDto>dto.backContent);
                break;
            case "video":
                flashcard.rightContentType = "video";
                flashcard.rightVideoContent = EditFlashcardVideoImpl.createFromDto(<FlashcardContentVideoDto>dto.backContent);
                break
        }

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

    static createFromDto(dto: FlashcardContentTextDto): EditFlashcardTextImpl {
        var text = new EditFlashcardTextImpl(dto.text);
        text.id = dto.id;
        text.languageId = dto.languageId;
        return text;
    }
} 

export class EditFlashcardImageImpl implements EditFlashcardImage {
    id: string = "unknown";
    imageUrl?: string;
    imageData?: string;

    getSrc(): string {
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

    static createFromDto(dto: FlashcardContentImageDto): EditFlashcardImageImpl {
        var image = new EditFlashcardImageImpl();
        image.id = dto.id;
        image.imageUrl = dto.imageUrl;
        return image;
    }

}

export class EditFlashcardAudioImpl {
    static createFromDto(dto: FlashcardContentAudioDto): EditFlashcardAudio {
        return new UrlAudioContent(dto.audioUrl);
    }
}

export class UrlAudioContent implements EditFlashcardAudio {
    constructor(private url: string) {}

    get type(): string {
        return 'url';
    }

    generateElement(): HTMLAudioElement {
        const audioElement = document.createElement('audio');
        audioElement.src = this.url;
        return audioElement;
    }

    generateBlob(): Blob {
        return new Blob();
    }
}

export class BlobAudioContent implements EditFlashcardAudio {
    constructor(private blob: Blob) {}

    get type(): string {
        return 'blob';
    }

    generateElement(): HTMLAudioElement {
        const audioElement = document.createElement('audio');
        audioElement.src = URL.createObjectURL(this.blob);
        return audioElement;
    }

    generateBlob(): Blob {
        return this.blob;
    }
}

export class FileAudioContent implements EditFlashcardAudio {
    constructor(private file: File) {}

    get type(): string {
        return 'file';
    }

    get name(): string {
        return this.file.name;
    }

    generateElement(): HTMLAudioElement {
        const audioElement = document.createElement('audio');
        audioElement.src = URL.createObjectURL(this.file);
        return audioElement;
    }

    generateBlob(): Blob {
        return this.file;
    }

    static isInstanceOf(obj: any) {
        return obj?.type === 'file';
    }
}

export class EditFlashcardVideoImpl implements EditFlashcardVideo {
    id: string = "unknown";
    videoUrl?: string;

    getSrc(): string {
        return this.videoUrl ?? "";
    }

    static createFromDto(dto: FlashcardContentVideoDto): EditFlashcardVideoImpl {
        var video = new EditFlashcardVideoImpl();
        video.id = dto.id;
        video.videoUrl = dto.videoUrl;
        return video;
    }

    static createFromFile(file: File): EditFlashcardVideoImpl {
        var video = new EditFlashcardVideoImpl();
        video.videoUrl = URL.createObjectURL(file);
        return video;
    }
}