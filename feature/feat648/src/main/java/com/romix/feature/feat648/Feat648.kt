package com.romix.feature.feat648

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat648Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat648UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat648FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat648UserSummary
)

data class Feat648UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat648NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat648Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat648Config = Feat648Config()
) {

    fun loadSnapshot(userId: Long): Feat648NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat648NetworkSnapshot(
            users = listOf(user),
            posts = posts,
            rawHash = hash
        )
    }

    private fun snapshotChecksum(user: ApiUserDto, posts: List<ApiPostDto>): Int {
        var result = 1
        result = 31 * result + user.id.hashCode()
        result = 31 * result + user.name.hashCode()
        for (post in posts) {
            result = 31 * result + post.id.hashCode()
            result = 31 * result + post.title.hashCode()
        }
        return result
    }

    fun toUserSummary(coreUser: CoreUser): Feat648UserSummary {
        return Feat648UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat648FeedItem> {
        val result = java.util.ArrayList<Feat648FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat648FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat648UiMapper {

    fun mapToUi(model: List<Feat648FeedItem>): Feat648UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat648UiModel(
            header = UiText("Feat648 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat648UiModel =
        Feat648UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat648UiModel =
        Feat648UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat648UiModel =
        Feat648UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat648Service(
    private val repository: Feat648Repository,
    private val uiMapper: Feat648UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat648UiModel {
        val snapshot = repository.loadSnapshot(userId)
        if (snapshot.users.isEmpty()) {
            return uiMapper.emptyState()
        }
        val coreUser = CoreUser(
            id = snapshot.users[0].id,
            name = snapshot.users[0].name,
            email = null,
            isActive = true
        )
        val items = repository.toFeedItems(listOf(coreUser))
        return uiMapper.mapToUi(items)
    }

    fun ping(path: String): Int {
        val request = NetworkRequest(
            path = path,
            method = "GET",
            body = null
        )
        val response = networkClient.execute(request)
        return response.code
    }

    fun demoComplexFlow(usersCount: Int): Feat648UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat648UserItem1(val user: CoreUser, val label: String)
data class Feat648UserItem2(val user: CoreUser, val label: String)
data class Feat648UserItem3(val user: CoreUser, val label: String)
data class Feat648UserItem4(val user: CoreUser, val label: String)
data class Feat648UserItem5(val user: CoreUser, val label: String)
data class Feat648UserItem6(val user: CoreUser, val label: String)
data class Feat648UserItem7(val user: CoreUser, val label: String)
data class Feat648UserItem8(val user: CoreUser, val label: String)
data class Feat648UserItem9(val user: CoreUser, val label: String)
data class Feat648UserItem10(val user: CoreUser, val label: String)

data class Feat648StateBlock1(val state: Feat648UiModel, val checksum: Int)
data class Feat648StateBlock2(val state: Feat648UiModel, val checksum: Int)
data class Feat648StateBlock3(val state: Feat648UiModel, val checksum: Int)
data class Feat648StateBlock4(val state: Feat648UiModel, val checksum: Int)
data class Feat648StateBlock5(val state: Feat648UiModel, val checksum: Int)
data class Feat648StateBlock6(val state: Feat648UiModel, val checksum: Int)
data class Feat648StateBlock7(val state: Feat648UiModel, val checksum: Int)
data class Feat648StateBlock8(val state: Feat648UiModel, val checksum: Int)
data class Feat648StateBlock9(val state: Feat648UiModel, val checksum: Int)
data class Feat648StateBlock10(val state: Feat648UiModel, val checksum: Int)

fun buildFeat648UserItem(user: CoreUser, index: Int): Feat648UserItem1 {
    return Feat648UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat648StateBlock(model: Feat648UiModel): Feat648StateBlock1 {
    return Feat648StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat648UserSummary> {
    val list = java.util.ArrayList<Feat648UserSummary>(users.size)
    for (user in users) {
        list += Feat648UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat648UserSummary>): List<UiListItem> {
    val items = java.util.ArrayList<UiListItem>(summaries.size)
    for ((index, summary) in summaries.withIndex()) {
        items += UiListItem(
            id = index.toLong(),
            title = summary.name,
            subtitle = if (summary.isActive) "Active" else "Inactive",
            selected = summary.isActive
        )
    }
    return items
}

fun createLargeUiModel(count: Int): Feat648UiModel {
    val summaries = (0 until count).map {
        Feat648UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat648UiModel(
        header = UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<CoreUser> {
    val list = java.util.ArrayList<CoreUser>(count)
    for (i in 0 until count) {
        list += CoreUser(
            id = i.toLong(),
            name = "User-$i",
            email = null,
            isActive = i % 3 != 0
        )
    }
    return list
}

fun mapToUiTextList(users: List<CoreUser>): List<UiText> {
    val list = java.util.ArrayList<UiText>(users.size)
    for (user in users) {
        list += UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<Feat648UiModel> {
    val models = java.util.ArrayList<Feat648UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat648AnalyticsEvent1(val name: String, val value: String)
data class Feat648AnalyticsEvent2(val name: String, val value: String)
data class Feat648AnalyticsEvent3(val name: String, val value: String)
data class Feat648AnalyticsEvent4(val name: String, val value: String)
data class Feat648AnalyticsEvent5(val name: String, val value: String)
data class Feat648AnalyticsEvent6(val name: String, val value: String)
data class Feat648AnalyticsEvent7(val name: String, val value: String)
data class Feat648AnalyticsEvent8(val name: String, val value: String)
data class Feat648AnalyticsEvent9(val name: String, val value: String)
data class Feat648AnalyticsEvent10(val name: String, val value: String)

fun logFeat648Event1(event: Feat648AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat648Event2(event: Feat648AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat648Event3(event: Feat648AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat648Event4(event: Feat648AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat648Event5(event: Feat648AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat648Event6(event: Feat648AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat648Event7(event: Feat648AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat648Event8(event: Feat648AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat648Event9(event: Feat648AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat648Event10(event: Feat648AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat648Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat648Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat648Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat648Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat648Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat648Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat648Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat648Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat648Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat648Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat648(u: CoreUser): Feat648Projection1 =
    Feat648Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat648Projection1> {
    val list = java.util.ArrayList<Feat648Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat648(u)
    }
    return list
}
