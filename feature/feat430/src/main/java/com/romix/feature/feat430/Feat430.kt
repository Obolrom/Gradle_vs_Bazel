package com.romix.feature.feat430

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat430Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat430UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat430FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat430UserSummary
)

data class Feat430UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat430NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat430Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat430Config = Feat430Config()
) {

    fun loadSnapshot(userId: Long): Feat430NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat430NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat430UserSummary {
        return Feat430UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat430FeedItem> {
        val result = java.util.ArrayList<Feat430FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat430FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat430UiMapper {

    fun mapToUi(model: List<Feat430FeedItem>): Feat430UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat430UiModel(
            header = UiText("Feat430 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat430UiModel =
        Feat430UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat430UiModel =
        Feat430UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat430UiModel =
        Feat430UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat430Service(
    private val repository: Feat430Repository,
    private val uiMapper: Feat430UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat430UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat430UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat430UserItem1(val user: CoreUser, val label: String)
data class Feat430UserItem2(val user: CoreUser, val label: String)
data class Feat430UserItem3(val user: CoreUser, val label: String)
data class Feat430UserItem4(val user: CoreUser, val label: String)
data class Feat430UserItem5(val user: CoreUser, val label: String)
data class Feat430UserItem6(val user: CoreUser, val label: String)
data class Feat430UserItem7(val user: CoreUser, val label: String)
data class Feat430UserItem8(val user: CoreUser, val label: String)
data class Feat430UserItem9(val user: CoreUser, val label: String)
data class Feat430UserItem10(val user: CoreUser, val label: String)

data class Feat430StateBlock1(val state: Feat430UiModel, val checksum: Int)
data class Feat430StateBlock2(val state: Feat430UiModel, val checksum: Int)
data class Feat430StateBlock3(val state: Feat430UiModel, val checksum: Int)
data class Feat430StateBlock4(val state: Feat430UiModel, val checksum: Int)
data class Feat430StateBlock5(val state: Feat430UiModel, val checksum: Int)
data class Feat430StateBlock6(val state: Feat430UiModel, val checksum: Int)
data class Feat430StateBlock7(val state: Feat430UiModel, val checksum: Int)
data class Feat430StateBlock8(val state: Feat430UiModel, val checksum: Int)
data class Feat430StateBlock9(val state: Feat430UiModel, val checksum: Int)
data class Feat430StateBlock10(val state: Feat430UiModel, val checksum: Int)

fun buildFeat430UserItem(user: CoreUser, index: Int): Feat430UserItem1 {
    return Feat430UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat430StateBlock(model: Feat430UiModel): Feat430StateBlock1 {
    return Feat430StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat430UserSummary> {
    val list = java.util.ArrayList<Feat430UserSummary>(users.size)
    for (user in users) {
        list += Feat430UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat430UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat430UiModel {
    val summaries = (0 until count).map {
        Feat430UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat430UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat430UiModel> {
    val models = java.util.ArrayList<Feat430UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat430AnalyticsEvent1(val name: String, val value: String)
data class Feat430AnalyticsEvent2(val name: String, val value: String)
data class Feat430AnalyticsEvent3(val name: String, val value: String)
data class Feat430AnalyticsEvent4(val name: String, val value: String)
data class Feat430AnalyticsEvent5(val name: String, val value: String)
data class Feat430AnalyticsEvent6(val name: String, val value: String)
data class Feat430AnalyticsEvent7(val name: String, val value: String)
data class Feat430AnalyticsEvent8(val name: String, val value: String)
data class Feat430AnalyticsEvent9(val name: String, val value: String)
data class Feat430AnalyticsEvent10(val name: String, val value: String)

fun logFeat430Event1(event: Feat430AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat430Event2(event: Feat430AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat430Event3(event: Feat430AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat430Event4(event: Feat430AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat430Event5(event: Feat430AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat430Event6(event: Feat430AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat430Event7(event: Feat430AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat430Event8(event: Feat430AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat430Event9(event: Feat430AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat430Event10(event: Feat430AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat430Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat430Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat430Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat430Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat430Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat430Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat430Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat430Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat430Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat430Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat430(u: CoreUser): Feat430Projection1 =
    Feat430Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat430Projection1> {
    val list = java.util.ArrayList<Feat430Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat430(u)
    }
    return list
}
