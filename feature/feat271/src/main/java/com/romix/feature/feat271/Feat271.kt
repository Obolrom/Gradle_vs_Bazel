package com.romix.feature.feat271

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat271Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat271UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat271FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat271UserSummary
)

data class Feat271UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat271NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat271Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat271Config = Feat271Config()
) {

    fun loadSnapshot(userId: Long): Feat271NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat271NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat271UserSummary {
        return Feat271UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat271FeedItem> {
        val result = java.util.ArrayList<Feat271FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat271FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat271UiMapper {

    fun mapToUi(model: List<Feat271FeedItem>): Feat271UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat271UiModel(
            header = UiText("Feat271 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat271UiModel =
        Feat271UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat271UiModel =
        Feat271UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat271UiModel =
        Feat271UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat271Service(
    private val repository: Feat271Repository,
    private val uiMapper: Feat271UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat271UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat271UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat271UserItem1(val user: CoreUser, val label: String)
data class Feat271UserItem2(val user: CoreUser, val label: String)
data class Feat271UserItem3(val user: CoreUser, val label: String)
data class Feat271UserItem4(val user: CoreUser, val label: String)
data class Feat271UserItem5(val user: CoreUser, val label: String)
data class Feat271UserItem6(val user: CoreUser, val label: String)
data class Feat271UserItem7(val user: CoreUser, val label: String)
data class Feat271UserItem8(val user: CoreUser, val label: String)
data class Feat271UserItem9(val user: CoreUser, val label: String)
data class Feat271UserItem10(val user: CoreUser, val label: String)

data class Feat271StateBlock1(val state: Feat271UiModel, val checksum: Int)
data class Feat271StateBlock2(val state: Feat271UiModel, val checksum: Int)
data class Feat271StateBlock3(val state: Feat271UiModel, val checksum: Int)
data class Feat271StateBlock4(val state: Feat271UiModel, val checksum: Int)
data class Feat271StateBlock5(val state: Feat271UiModel, val checksum: Int)
data class Feat271StateBlock6(val state: Feat271UiModel, val checksum: Int)
data class Feat271StateBlock7(val state: Feat271UiModel, val checksum: Int)
data class Feat271StateBlock8(val state: Feat271UiModel, val checksum: Int)
data class Feat271StateBlock9(val state: Feat271UiModel, val checksum: Int)
data class Feat271StateBlock10(val state: Feat271UiModel, val checksum: Int)

fun buildFeat271UserItem(user: CoreUser, index: Int): Feat271UserItem1 {
    return Feat271UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat271StateBlock(model: Feat271UiModel): Feat271StateBlock1 {
    return Feat271StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat271UserSummary> {
    val list = java.util.ArrayList<Feat271UserSummary>(users.size)
    for (user in users) {
        list += Feat271UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat271UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat271UiModel {
    val summaries = (0 until count).map {
        Feat271UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat271UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat271UiModel> {
    val models = java.util.ArrayList<Feat271UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat271AnalyticsEvent1(val name: String, val value: String)
data class Feat271AnalyticsEvent2(val name: String, val value: String)
data class Feat271AnalyticsEvent3(val name: String, val value: String)
data class Feat271AnalyticsEvent4(val name: String, val value: String)
data class Feat271AnalyticsEvent5(val name: String, val value: String)
data class Feat271AnalyticsEvent6(val name: String, val value: String)
data class Feat271AnalyticsEvent7(val name: String, val value: String)
data class Feat271AnalyticsEvent8(val name: String, val value: String)
data class Feat271AnalyticsEvent9(val name: String, val value: String)
data class Feat271AnalyticsEvent10(val name: String, val value: String)

fun logFeat271Event1(event: Feat271AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat271Event2(event: Feat271AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat271Event3(event: Feat271AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat271Event4(event: Feat271AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat271Event5(event: Feat271AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat271Event6(event: Feat271AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat271Event7(event: Feat271AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat271Event8(event: Feat271AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat271Event9(event: Feat271AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat271Event10(event: Feat271AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat271Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat271Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat271Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat271Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat271Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat271Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat271Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat271Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat271Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat271Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat271(u: CoreUser): Feat271Projection1 =
    Feat271Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat271Projection1> {
    val list = java.util.ArrayList<Feat271Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat271(u)
    }
    return list
}
