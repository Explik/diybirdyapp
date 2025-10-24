// =================================================================================================
// ViewModel interfaces 

import { environment } from "../../../../environments/environment";
import { FlashcardContentAudioDto, FlashcardContentDto, FlashcardContentImageDto, FlashcardContentTextDto, FlashcardContentUploadAudioDto, FlashcardContentUploadImageDto, FlashcardContentUploadVideoDto, FlashcardContentVideoDto, FlashcardDeckDto, FlashcardDto, FlashcardLanguageDto } from "../../../shared/api-client";
import { RecursivePartial } from "../../../shared/models/util.model";

// =================================================================================================
export interface EditFlashcardDeck extends Partial<FlashcardDeckDto> {
    get name(): string;
    get flashcards(): EditFlashcard[];

    getAllChanges(): Partial<FlashcardDeckDto> | undefined;
}

export interface EditFlashcardLanguage extends Partial<FlashcardLanguageDto> {

}

export type FlashcardStates = "unchanged" | "added" | "updated" | "deleted";
export type FlashcardContentTypes = "text" | "image" | "audio" | "video";

export interface EditFlashcard extends Partial<FlashcardDto> {
    get state(): FlashcardStates;

    leftContentType: FlashcardContentTypes;
    rightContentType: FlashcardContentTypes;

    leftTextContent?: EditFlashcardText;
    rightTextContent?: EditFlashcardText;

    leftImageContent?: EditFlashcardImage;
    rightImageContent?: EditFlashcardImage;

    leftAudioContent?: EditFlashcardAudio;
    rightAudioContent?: EditFlashcardAudio;

    leftVideoContent?: EditFlashcardVideo;
    rightVideoContent?: EditFlashcardVideo;

    getAllChanges(): FlashcardChanges | undefined;
}

export interface EditFlashcardText extends Partial<FlashcardContentTextDto> {
    getChanges(): FlashcardContentChanges | undefined;
}

export interface EditFlashcardImage extends Partial<FlashcardContentImageDto> {
    getSrc(): string;
    getChanges(): FlashcardContentChanges | undefined;
}

export interface EditFlashcardAudio extends Partial<FlashcardContentAudioDto> {
    generateElement(): HTMLAudioElement;
    getChanges(): FlashcardContentChanges | undefined;
}

export interface EditFlashcardVideo extends Partial<FlashcardContentVideoDto> {
    getSrc(): string;
    getChanges(): FlashcardContentChanges | undefined;
}

export interface FlashcardChanges {
    get state(): FlashcardStates;
    get flashcard(): RecursivePartial<FlashcardDto>;
    get files(): File[];
}

export interface FlashcardContentChanges {
    get content(): Partial<FlashcardContentDto>;
    get files(): File[];
}

// =================================================================================================
// ViewModel implementations
// =================================================================================================
export class EditFlashcardDeckImpl implements EditFlashcardDeck {
    tracker: ChangeTracker = new ChangeTracker();

    get id(): string { return this.tracker.get(this, 'id'); }
    set id(value: string) { this.tracker.set(this, 'id', value); }

    get name(): string { return this.tracker.get(this, 'name'); }
    set name(value: string) { this.tracker.set(this, 'name', value); }

    get description(): string | undefined { return this.tracker.get(this, 'description'); }
    set description(value: string | undefined) { this.tracker.set(this, 'description', value); }

    get flashcards(): EditFlashcardImpl[] { return this.tracker.get(this, 'flashcards'); }
    set flashcards(value: EditFlashcardImpl[]) { this.tracker.set(this, 'flashcards', value); }

    static createFromDto(dto: FlashcardDeckDto): EditFlashcardDeckImpl {
        var deck = new EditFlashcardDeckImpl();
        deck.id = dto.id;
        deck.name = dto.name || "";
        deck.description = dto.description;
        deck.flashcards = [];
        return deck;
    }

    getAllChanges(): Partial<FlashcardDeckDto> | undefined {
        return this.tracker.getAllChanges({ id: this.id });
    }
}

export class EditFlashcardLanguageImpl implements EditFlashcardLanguage {
    tracker: ChangeTracker = new ChangeTracker();

    get id(): string { return this.tracker.get(this, 'id'); }
    set id(value: string) { this.tracker.set(this, 'id', value); }

    get name(): string { return this.tracker.get(this, 'name'); }
    set name(value: string) { this.tracker.set(this, 'name', value); }

    get abbreviation(): string { return this.tracker.get(this, 'abbreviation'); }
    set abbreviation(value: string) { this.tracker.set(this, 'abbreviation', value); }

    static createFromDto(dto: FlashcardLanguageDto): EditFlashcardLanguageImpl {
        var language = new EditFlashcardLanguageImpl();
        language.id = dto.id;
        language.name = dto.name;
        language.abbreviation = dto.abbreviation;
        return language;
    }

    getAllChanges(): Partial<FlashcardLanguageDto> | undefined {
        return this.tracker.getAllChanges();
    }
}

export class EditFlashcardImpl implements EditFlashcard {
    _deckOrder: number | undefined;
    
    id: string = "unknown";
    state: FlashcardStates = "unchanged";

    deckId?: string | undefined;
    get deckOrder(): number | undefined { return this._deckOrder; }
    set deckOrder(value: number | undefined) { 
        if (this._deckOrder !== value && this.state === "unchanged")
            this.state = "updated"; 
        
        this._deckOrder = value;    
    }

    frontContent?: FlashcardContentDto | undefined;
    backContent?: FlashcardContentDto | undefined;

    leftContentType: FlashcardContentTypes = "text";
    rightContentType: FlashcardContentTypes = "text";

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

    public static createDefault(): EditFlashcardImpl {
        var flashcard = new EditFlashcardImpl();
        flashcard.id = "new-id";
        flashcard._deckOrder = undefined;

        flashcard.leftContentType = "text";
        flashcard.rightContentType = "text";
        flashcard.leftTextContent = EditFlashcardTextImpl.create();
        flashcard.rightTextContent = EditFlashcardTextImpl.create();

        return flashcard;
    }

    public static createFromDto(dto: FlashcardDto): EditFlashcardImpl {
        var flashcard = new EditFlashcardImpl();
        flashcard.id = dto.id;
        flashcard.deckId = dto.deckId;
        flashcard._deckOrder = dto.deckOrder;
        flashcard.frontContent = dto.frontContent;
        flashcard.backContent = dto.backContent;

        switch (dto.frontContent!.type) {
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

        switch (dto.backContent!.type) {
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

    getAllChanges(): FlashcardChanges | undefined {
        // Collect content changes 
        let leftChanges = undefined;
        let rightChanges = undefined;

        switch (this.leftContentType) {
            case "audio":
                leftChanges = this.leftAudioContent!.getChanges();
                break;
            case "text":
                leftChanges = this.leftTextContent!.getChanges();
                break;
            case "image":
                leftChanges = this.leftImageContent!.getChanges();
                break;
            case "video":
                leftChanges = this.leftVideoContent!.getChanges();
                break;
        }

        switch (this.rightContentType) {
            case "audio":
                rightChanges = this.rightAudioContent!.getChanges();
                break;
            case "text":
                rightChanges = this.rightTextContent!.getChanges();
                break;
            case "image":
                rightChanges = this.rightImageContent!.getChanges();
                break;
            case "video":
                rightChanges = this.rightVideoContent!.getChanges();
                break;
        }

        if (this.state === "unchanged" && !leftChanges && !rightChanges)
            return undefined;

        return {
            state: this.state,
            flashcard: {
                id: this.id,
                deckId: this.state === 'added' ? this.deckId : undefined,
                deckOrder: this.deckOrder,
                frontContent: leftChanges?.content as any,
                backContent: rightChanges?.content as any
            },
            files: [...(leftChanges?.files ?? []), ...(rightChanges?.files ?? [])]
        };
    }
}

export class EditFlashcardTextImpl implements EditFlashcardText {
    tracker: ChangeTracker = new ChangeTracker();

    get id(): string { return this.tracker.get(this, 'id'); }
    set id(value: string) { this.tracker.set(this, 'id', value); }

    get text(): string { return this.tracker.get(this, 'text'); }
    set text(value: string) { this.tracker.set(this, 'text', value); }

    get languageId(): string | undefined { return this.tracker.get(this, 'languageId'); }
    set languageId(value: string | undefined) { this.tracker.set(this, 'languageId', value); }

    getChanges(): FlashcardContentChanges | undefined {
        let changes = this.tracker.getAllChanges({ id: this.id, type: "text" });
        
        if (!changes)
            return undefined;

        return {
            content: changes,
            files: []
        };
    }

    static create(): EditFlashcardTextImpl {
        var text = new EditFlashcardTextImpl();
        text.id = "new-id";
        text.text = "";
        text.languageId = "";
        return text;
    }

    static createFromDto(dto: FlashcardContentTextDto): EditFlashcardTextImpl {
        var text = new EditFlashcardTextImpl();
        text.id = dto.id;
        text.text = dto.text;
        text.languageId = dto.languageId;
        return text;
    }
}

export class EditFlashcardImageImpl implements EditFlashcardImage {
    id: string = "unknown";
    imageUrl?: string;
    imageFile?: File;

    getSrc(): string {
        if (this.imageUrl) {
            const isFullyDefined = this.imageUrl.startsWith("http") || this.imageUrl.startsWith("blob:");
            return isFullyDefined ? this.imageUrl : environment.apiUrl + "/" + this.imageUrl
        }
        return URL.createObjectURL(this.imageFile!) ?? "";
    }

    generateBlob(): Blob {
        return this.imageFile!;
    }

    getChanges(): FlashcardContentChanges | undefined {
        if (!this.imageFile)
            return undefined;

        return {
            content: {
                id: this.id,
                type: "image-upload",
                imageFileName: this.imageFile.name
            } as FlashcardContentUploadImageDto,
            files: [this.imageFile]
        };
    }

    static createFromFile(file: File): EditFlashcardImageImpl {
        var image = new EditFlashcardImageImpl();
        image.imageFile = file;
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
    constructor(private url: string) { }

    get type(): string {
        return 'url';
    }

    generateElement(): HTMLAudioElement {
        const audioElement = document.createElement('audio');
        audioElement.src = this.url?.startsWith("http") ? this.url : environment.apiUrl + "/" + this.url;
        return audioElement;
    }

    getChanges(): FlashcardContentChanges | undefined {
        return undefined;
    }
}

export class BlobAudioContent implements EditFlashcardAudio {
    constructor(private fileName: string, private blob: Blob) { }

    get type(): string {
        return 'blob';
    }

    generateElement(): HTMLAudioElement {
        const audioElement = document.createElement('audio');
        audioElement.src = URL.createObjectURL(this.blob);
        return audioElement;
    }

    getChanges(): FlashcardContentChanges | undefined {
        return {
            content: {
                id: "new-id",
                type: "audio-upload",
                audioFileName: this.fileName,
                languageId: "langVertex1"
            } as FlashcardContentUploadAudioDto,
            files: [new File([this.blob], this.fileName)]
        };
    }
}

export class FileAudioContent implements EditFlashcardAudio {
    constructor(private file: File) { }

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

    getChanges(): FlashcardContentChanges | undefined {
        return {
            content: {
                id: "new-id",
                type: "audio-upload",
                languageId: "langVertex1",
                audioFileName: this.file.name
            } as FlashcardContentUploadAudioDto,
            files: [this.file]
        };
    }

    static isInstanceOf(obj: any) {
        return obj?.type === 'file';
    }
}

export class EditFlashcardVideoImpl implements EditFlashcardVideo {
    id: string = "unknown";
    videoUrl?: string;
    videoFile?: File;

    getSrc(): string {
        if (this.videoUrl) {
            const isFullyDefined = this.videoUrl.startsWith("http") || this.videoUrl.startsWith("blob:");
            return isFullyDefined ? this.videoUrl : environment.apiUrl + "/" + this.videoUrl;
        }
        return "";
    }

    getChanges(): FlashcardContentChanges | undefined {
        if (!this.videoFile)
            return undefined; 

        return {
            content: {
                id: this.id,
                type: "video-upload",
                languageId: "langVertex1",
                videoFileName: this.videoFile.name
            } as FlashcardContentUploadVideoDto,
            files: [this.videoFile]
        };
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
        video.videoFile = file;
        return video;
    }
}

export class ChangeTracker {
    data: { [key: string]: { originalValue: any, currentValue: any } } = {};

    get<T>(obj: any, key: string): T {
        if (!this.data.hasOwnProperty(key))
            throw new Error(`Key ${key} not found in data`);

        return this.data[key].currentValue;
    }

    set<T>(obj: any, key: string, value: T): void {
        if (!this.data.hasOwnProperty(key)) {
            this.data[key] = {
                originalValue: value,
                currentValue: value
            };
        }
        else {
            this.data[key].currentValue = value;
        }
    }

    getAllChanges<T>(init?: Partial<T>): Partial<T> | undefined {
        let hasChanges = false;
        let buffer = init ? { ...init } : {} as Partial<T>;

        for (let key in this.data) {
            let isDirty = this.data[key].currentValue !== this.data[key].originalValue;
            if (isDirty) {
                hasChanges = true;
                (buffer as any)[key] = this.data[key].currentValue;
            }
        }
        return hasChanges ? buffer : undefined;
    }
}