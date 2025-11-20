package com.romix.feature.feat532

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat532Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat532UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat532FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat532UserSummary
)

data class Feat532UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat532NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat532Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat532Config = Feat532Config()
) {

    fun loadSnapshot(userId: Long): Feat532NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat532NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat532UserSummary {
        return Feat532UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat532FeedItem> {
        val result = java.util.ArrayList<Feat532FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat532FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat532UiMapper {

    fun mapToUi(model: List<Feat532FeedItem>): Feat532UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat532UiModel(
            header = UiText("Feat532 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat532UiModel =
        Feat532UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat532UiModel =
        Feat532UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat532UiModel =
        Feat532UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat532Service(
    private val repository: Feat532Repository,
    private val uiMapper: Feat532UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat532UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat532UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat532UserItem1(val user: CoreUser, val label: String)
data class Feat532UserItem2(val user: CoreUser, val label: String)
data class Feat532UserItem3(val user: CoreUser, val label: String)
data class Feat532UserItem4(val user: CoreUser, val label: String)
data class Feat532UserItem5(val user: CoreUser, val label: String)
data class Feat532UserItem6(val user: CoreUser, val label: String)
data class Feat532UserItem7(val user: CoreUser, val label: String)
data class Feat532UserItem8(val user: CoreUser, val label: String)
data class Feat532UserItem9(val user: CoreUser, val label: String)
data class Feat532UserItem10(val user: CoreUser, val label: String)

data class Feat532StateBlock1(val state: Feat532UiModel, val checksum: Int)
data class Feat532StateBlock2(val state: Feat532UiModel, val checksum: Int)
data class Feat532StateBlock3(val state: Feat532UiModel, val checksum: Int)
data class Feat532StateBlock4(val state: Feat532UiModel, val checksum: Int)
data class Feat532StateBlock5(val state: Feat532UiModel, val checksum: Int)
data class Feat532StateBlock6(val state: Feat532UiModel, val checksum: Int)
data class Feat532StateBlock7(val state: Feat532UiModel, val checksum: Int)
data class Feat532StateBlock8(val state: Feat532UiModel, val checksum: Int)
data class Feat532StateBlock9(val state: Feat532UiModel, val checksum: Int)
data class Feat532StateBlock10(val state: Feat532UiModel, val checksum: Int)

fun buildFeat532UserItem(user: CoreUser, index: Int): Feat532UserItem1 {
    return Feat532UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat532StateBlock(model: Feat532UiModel): Feat532StateBlock1 {
    return Feat532StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat532UserSummary> {
    val list = java.util.ArrayList<Feat532UserSummary>(users.size)
    for (user in users) {
        list += Feat532UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat532UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat532UiModel {
    val summaries = (0 until count).map {
        Feat532UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat532UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat532UiModel> {
    val models = java.util.ArrayList<Feat532UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat532AnalyticsEvent1(val name: String, val value: String)
data class Feat532AnalyticsEvent2(val name: String, val value: String)
data class Feat532AnalyticsEvent3(val name: String, val value: String)
data class Feat532AnalyticsEvent4(val name: String, val value: String)
data class Feat532AnalyticsEvent5(val name: String, val value: String)
data class Feat532AnalyticsEvent6(val name: String, val value: String)
data class Feat532AnalyticsEvent7(val name: String, val value: String)
data class Feat532AnalyticsEvent8(val name: String, val value: String)
data class Feat532AnalyticsEvent9(val name: String, val value: String)
data class Feat532AnalyticsEvent10(val name: String, val value: String)

fun logFeat532Event1(event: Feat532AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat532Event2(event: Feat532AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat532Event3(event: Feat532AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat532Event4(event: Feat532AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat532Event5(event: Feat532AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat532Event6(event: Feat532AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat532Event7(event: Feat532AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat532Event8(event: Feat532AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat532Event9(event: Feat532AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat532Event10(event: Feat532AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat532Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat532Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat532Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat532Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat532Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat532Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat532Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat532Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat532Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat532Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat532(u: CoreUser): Feat532Projection1 =
    Feat532Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat532Projection1> {
    val list = java.util.ArrayList<Feat532Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat532(u)
    }
    return list
}
