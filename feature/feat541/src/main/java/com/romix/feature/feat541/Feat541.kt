package com.romix.feature.feat541

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat541Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat541UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat541FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat541UserSummary
)

data class Feat541UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat541NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat541Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat541Config = Feat541Config()
) {

    fun loadSnapshot(userId: Long): Feat541NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat541NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat541UserSummary {
        return Feat541UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat541FeedItem> {
        val result = java.util.ArrayList<Feat541FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat541FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat541UiMapper {

    fun mapToUi(model: List<Feat541FeedItem>): Feat541UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat541UiModel(
            header = UiText("Feat541 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat541UiModel =
        Feat541UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat541UiModel =
        Feat541UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat541UiModel =
        Feat541UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat541Service(
    private val repository: Feat541Repository,
    private val uiMapper: Feat541UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat541UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat541UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat541UserItem1(val user: CoreUser, val label: String)
data class Feat541UserItem2(val user: CoreUser, val label: String)
data class Feat541UserItem3(val user: CoreUser, val label: String)
data class Feat541UserItem4(val user: CoreUser, val label: String)
data class Feat541UserItem5(val user: CoreUser, val label: String)
data class Feat541UserItem6(val user: CoreUser, val label: String)
data class Feat541UserItem7(val user: CoreUser, val label: String)
data class Feat541UserItem8(val user: CoreUser, val label: String)
data class Feat541UserItem9(val user: CoreUser, val label: String)
data class Feat541UserItem10(val user: CoreUser, val label: String)

data class Feat541StateBlock1(val state: Feat541UiModel, val checksum: Int)
data class Feat541StateBlock2(val state: Feat541UiModel, val checksum: Int)
data class Feat541StateBlock3(val state: Feat541UiModel, val checksum: Int)
data class Feat541StateBlock4(val state: Feat541UiModel, val checksum: Int)
data class Feat541StateBlock5(val state: Feat541UiModel, val checksum: Int)
data class Feat541StateBlock6(val state: Feat541UiModel, val checksum: Int)
data class Feat541StateBlock7(val state: Feat541UiModel, val checksum: Int)
data class Feat541StateBlock8(val state: Feat541UiModel, val checksum: Int)
data class Feat541StateBlock9(val state: Feat541UiModel, val checksum: Int)
data class Feat541StateBlock10(val state: Feat541UiModel, val checksum: Int)

fun buildFeat541UserItem(user: CoreUser, index: Int): Feat541UserItem1 {
    return Feat541UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat541StateBlock(model: Feat541UiModel): Feat541StateBlock1 {
    return Feat541StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat541UserSummary> {
    val list = java.util.ArrayList<Feat541UserSummary>(users.size)
    for (user in users) {
        list += Feat541UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat541UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat541UiModel {
    val summaries = (0 until count).map {
        Feat541UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat541UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat541UiModel> {
    val models = java.util.ArrayList<Feat541UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat541AnalyticsEvent1(val name: String, val value: String)
data class Feat541AnalyticsEvent2(val name: String, val value: String)
data class Feat541AnalyticsEvent3(val name: String, val value: String)
data class Feat541AnalyticsEvent4(val name: String, val value: String)
data class Feat541AnalyticsEvent5(val name: String, val value: String)
data class Feat541AnalyticsEvent6(val name: String, val value: String)
data class Feat541AnalyticsEvent7(val name: String, val value: String)
data class Feat541AnalyticsEvent8(val name: String, val value: String)
data class Feat541AnalyticsEvent9(val name: String, val value: String)
data class Feat541AnalyticsEvent10(val name: String, val value: String)

fun logFeat541Event1(event: Feat541AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat541Event2(event: Feat541AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat541Event3(event: Feat541AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat541Event4(event: Feat541AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat541Event5(event: Feat541AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat541Event6(event: Feat541AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat541Event7(event: Feat541AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat541Event8(event: Feat541AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat541Event9(event: Feat541AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat541Event10(event: Feat541AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat541Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat541Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat541Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat541Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat541Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat541Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat541Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat541Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat541Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat541Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat541(u: CoreUser): Feat541Projection1 =
    Feat541Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat541Projection1> {
    val list = java.util.ArrayList<Feat541Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat541(u)
    }
    return list
}
