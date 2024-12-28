package ma.ensa.mobile.profit.models;

public class Goal {
    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private Long userId; // ID de l'utilisateur associé

    // Constructeurs
    public Goal() {}

    public Goal(String title, String description, Long userId) {
        this.title = title;
        this.description = description;
        this.userId = userId;
        this.completed = false; // Par défaut, un objectif n'est pas complété
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}